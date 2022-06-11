package com.x.doraemon.file;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.nio.file.SensitivityWatchEventModifier;
import com.x.doraemon.therad.LoadBalanceExecutor;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 文件夹监视器
 * <p>
 * 1、对所监视的文件夹会进行递归监听子文件夹的生成，如：复制或创建文件夹
 * <p>
 * 2、对文件夹的递归监视，会出现无法操作文件夹，原因如下：
 * <p>
 * WatchService实现将打开该目录的句柄(Windows的工作方式),该打开句柄不会阻止目录被删除，但是会阻止目录的【父目录】立即被删除。删除监视目录后，该句柄将立即关闭。
 * <p>
 * 3、如果存在递归监视情况，要对文件夹操作，需要从子文件夹开始删除，否则无法操作父文件夹
 * <p>
 * @Author AD
 * @Date 2020-3-25 10:08
 */
public class FolderMonitor implements Comparable<FolderMonitor> {

    // ---------------------------------- 静态常量 ----------------------------------

    private static final WatchEvent.Kind[] events = new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW};

    // ---------------------------------- 成员变量 ----------------------------------

    private volatile boolean started = false;

    private Path dir;

    private final WatchService watcher;

    private final IFileListener listener;

    private final ExecutorService monitor;

    private final Cache<String, Path> modifyEventCache;

    private final Set<String> validFiles;

    private final Set<String> subDirs;

    private static Map<String, FolderMonitor> monitors = new ConcurrentHashMap<>();

    // ---------------------------------- 构造方法 ----------------------------------

    public static FolderMonitor get(Path dir, IFileListener listener) throws Exception {
        // File的hashcode值为path小写值的hashcode
        if (monitors.containsKey(getAbsPath(dir))) {
            return monitors.get(getAbsPath(dir));
        } else {
            return new FolderMonitor(dir, listener);
        }
    }

    /**
     * 构造方法
     * @param dir   需要监控的文件夹
     * @param listener 监听器对象
     * @throws Exception
     */
    private FolderMonitor(Path dir, IFileListener listener) throws Exception {
        if (!Files.isDirectory(dir)) {
            throw new Exception("dir isn't a directory");
        }
        this.dir = dir;
        this.watcher = FileSystems.getDefault().newWatchService();
        dir.register(watcher, events, SensitivityWatchEventModifier.LOW);
        this.listener = listener;
        this.monitor = new LoadBalanceExecutor<>("runner", 1);
        this.validFiles = new ConcurrentSkipListSet<>();
        this.subDirs = new ConcurrentSkipListSet<>();
        this.modifyEventCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
        subDirs.add(getAbsPath(dir));
        monitors.put(getAbsPath(dir), this);
    }

    // ---------------------------------- 成员方法 ----------------------------------
    public synchronized void start() {
        if (started) {
            return;
        }
        this.started = true;
        // 启动时扫描子文件夹
        initDirMonitor();
        monitor.execute(() -> {
            while (started && !monitor.isShutdown()) {
                /*
                 * take：获取变化信息的监控池，没有则一直等待，适合长时间监控
                 * poll：获取变化信息的监控池，没有则返回null,适合某个时间点监控
                 */
                WatchKey key = null;
                try {
                    key = watcher.take();
                    List<WatchEvent<?>> events = key.pollEvents();
                    if (isRename(events)) {
                        Path oldPath = getPathFromEvent(events.get(0));
                        Path newPath = getPathFromEvent(events.get(1));
                        if (isDir(oldPath)) {
                            onDirRename(oldPath, newPath);
                        } else {
                            onFileRename(oldPath, newPath);
                        }
                    } else {
                        // file.exist()当文件被删除时，只是被标记为删除，直到所有的句柄关闭，才返回false，否则为true。所以用这个方法判断文件是否存在不准确
                        if (events.size() == 0) {
                            // 根文件夹被删除
                            onDirDelete(dir);
                        } else {
                            for (WatchEvent event : events) {
                                Path context = (Path) event.context();
                                // filename是当前监控的文件夹(dir)的一级事件名(文件|文件夹),不能获取到多层级的事件信息
                                String filename = context.getFileName().toString();
                                if (isInvalidEvent(filename)) {
                                    continue;
                                }
                                Path change = Paths.get(getAbsPath(dir), filename);
                                WatchEvent.Kind kind = event.kind();// modify、create、delete、overflow
                                switch (kind.name()) {
                                    case WatchEventEnum.CREATE:
                                        if (Files.isDirectory(change)) {
                                            onDirCreate(change);
                                        } else {
                                            onFileCreate(change);
                                        }
                                        break;
                                    // 文件重新命名并不是modify,而是delete+create.这里指文件内容的修改
                                    case WatchEventEnum.MODIFY:
                                        onFileModify(change);
                                        break;
                                    case WatchEventEnum.DELETE:
                                        if (isDir(change)) {
                                            // 自身删除事件会先触发events=0的事件，父监听器会在触发一次
                                            onDirDelete(change);
                                        } else {
                                            onFileDelete(change);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                } catch (ClosedWatchServiceException ce) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                } finally {
                    /*
                     * -每次take()\poll()都会导致线程监控阻塞，操作文件可能时间长，
                     *  如果监听目录下有其他事件发生，将会导致事件丢失。
                     * -重置操作表示重启该线程，后续的事件都会被读到。
                     */
                    if (key != null) {
                        key.reset();
                    }
                }
            }
        });
    }

    public synchronized void stop() {
        if (!started) {
            return;
        }
        try {
            started = false;
            validFiles.clear();
            subDirs.clear();
            watcher.close();
            monitor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------- 私有方法 ----------------------------------


    private void onFileRename(Path oldFile, Path newFile) {
        listener.onFileRename(oldFile, newFile);
    }


    private void onFileDelete(Path file) {
        if (Files.notExists(file)) {
            listener.onFileDelete(file);
        }
    }

    private void onFileCreate(Path file) throws IOException {
        /*
         - 修改已存在文件时,会产生modify、create事件,需过滤create
         - 如果是复制,会产生create、modify事件，复制由modify处理
        */
        if (Files.exists(file) && !Files.isDirectory(file)) {
            listener.onFileCreate(file);
            if (Files.size(file) > 0) {
                modifyEventCache.put(file.toAbsolutePath().toString(), file);
                listener.onFileModify(file);
            }
        }
    }

    private void onFileModify(Path file) throws IOException {
        if (Files.exists(file) && !Files.isDirectory(file)) {
            // 修改时，会触发2次修改事件
            Path oldFile = modifyEventCache.getIfPresent(getAbsPath(file));
            // 同一个事件在2s内没有处理过（不为null，表示在2s内处理过，不进行处理）
            if (oldFile == null) {
                // 标识已处理该事件
                modifyEventCache.put(getAbsPath(file), file);
                // 判断文件内容是否为：有效修改
                if (Files.size(file) > 0) {
                    // 缓存有效文件名称
                    validFiles.add(getAbsPath(file));
                    // 通知修改内容
                    listener.onFileModify(file);
                }
                // 文件内容为空，可能是全删除，也可能是复制空文件（复制的空文件不触发modify，全删除内容则触发）
                else {
                    // 文件之前有内容，现在全删除了
                    if (validFiles.contains(getAbsPath(file))) {
                        // 通知修改
                        listener.onFileModify(file);
                    } else {
                        // 标识该文件为无效文件
                        validFiles.remove(getAbsPath(file));
                    }
                }
            }
        }
    }

    private void onDirRename(Path oldDir, Path newDir) {
        subDirs.remove(getAbsPath(oldDir));
        subDirs.add(getAbsPath(newDir));
        FolderMonitor oldMonitor = monitors.remove(oldDir);
        if (oldMonitor != null) {
            try {
                FolderMonitor newMonitor = get(newDir, oldMonitor.listener);
                if (oldMonitor.started) {
                    oldMonitor.stop();
                    newMonitor.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        listener.onDirRename(oldDir, newDir);
    }

    private void onDirCreate(Path dir) throws Exception {
        subDirs.add(getAbsPath(dir));
        listener.onDirCreate(dir);
    }


    private void onDirDelete(Path dir) {
        if (isDir(dir)) {
            subDirs.remove(getAbsPath(dir));
            listener.onDirDelete(dir);
            FolderMonitor monitor = monitors.remove(getAbsPath(dir));
            if (monitor != null) {
                if (monitor.started) {
                    monitor.stop();
                }
            }
        }
    }

    private void initDirMonitor() {
        // 获取下级文件夹，没有递归获取
        try {
            Stream<Path> paths = Files.list(dir);
            paths.filter(p -> Files.isDirectory(p)).forEach(p -> subDirs.add(getAbsPath(p)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean isInvalidEvent(String filename) {
        if (filename.contains("___jb_old___") || filename.contains("___jb_tmp___")) {
            return true;
        }
        return false;
    }

    private boolean isRename(List<WatchEvent<?>> events) {
        if (events.size() == 2) {
            WatchEvent<?> delete = events.get(0);
            WatchEvent<?> create = events.get(1);
            if (delete.kind().name().equals(WatchEventEnum.DELETE) && create.kind().name().equals(WatchEventEnum.CREATE)) {
                return true;
            }
        }
        return false;
    }

    private Path getPathFromEvent(WatchEvent<?> event) {
        // 当前path只是获取该事件的文件名,并不是绝对路径,需修正
        Path path = (Path) event.context();
        // 修改为绝对路径
        return dir.resolve(path.getFileName());
    }

    private boolean isDir(Path path) {
        if (subDirs.contains(getAbsPath(path))) {
            return true;
        }
        return false;
    }

    private static String getAbsPath(Path path) {
        return path.toAbsolutePath().toString();
    }

    @Override
    public int compareTo(FolderMonitor o) {
        return dir.compareTo(o.dir);
    }

    private static class WatchEventEnum {

        private static final String MODIFY = "ENTRY_MODIFY";
        private static final String CREATE = "ENTRY_CREATE";
        private static final String DELETE = "ENTRY_DELETE";
    }

}
