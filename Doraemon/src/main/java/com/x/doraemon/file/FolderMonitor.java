package com.x.doraemon.file;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sun.nio.file.SensitivityWatchEventModifier;
import com.x.doraemon.therad.LoadBalanceExecutor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
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
 *
 * @Author AD
 * @Date 2020-3-25 10:08
 */
public class FolderMonitor implements Comparable<FolderMonitor> {

    // ---------------------------------- 静态常量 ----------------------------------

    private static final WatchEvent.Kind[] events = new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW};

    // ---------------------------------- 成员变量 ----------------------------------

    private volatile boolean started = false;

    private File folder;

    private final WatchService watcher;

    private final IFileListener listener;

    private final ExecutorService monitor;

    private final Cache<String, File> modifyEventCache;

    private final Set<String> validFiles;

    private final Set<String> subFolders;

    private static Map<File, FolderMonitor> monitors = new ConcurrentHashMap<>();

    // ---------------------------------- 构造方法 ----------------------------------

    public static FolderMonitor get(File folder, IFileListener listener) throws Exception {
        // File的hashcode值为path小写值的hashcode
        if (monitors.containsKey(folder)) {
            return monitors.get(folder);
        } else {
            return new FolderMonitor(folder, listener);
        }
    }

    /**
     * 构造方法
     *
     * @param folder   需要监控的文件夹
     * @param listener 监听器对象
     * @throws Exception
     */
    private FolderMonitor(File folder, IFileListener listener) throws Exception {
        this.folder = folder;
        Path path = Paths.get(folder.getPath());
        this.watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, events, SensitivityWatchEventModifier.LOW);
        this.listener = listener;
        this.monitor = new LoadBalanceExecutor<>("runner", 1);
        this.validFiles = new ConcurrentSkipListSet<>();
        this.subFolders = new ConcurrentSkipListSet<>();
        this.modifyEventCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
        subFolders.add(folder.getAbsolutePath());
        monitors.put(folder, this);
    }

    // ---------------------------------- 成员方法 ----------------------------------
    public synchronized void start() {
        if (started) {
            return;
        }
        this.started = true;
        // 启动时扫描子文件夹
        initFolderMonitor();
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
                        String oldPath = getPathFromEvent(events.get(0));
                        String newPath = getPathFromEvent(events.get(1));
                        if (isFolder(oldPath)) {
                            onFolderRename(oldPath, newPath);
                        } else {
                            onFileRename(oldPath, newPath);
                        }
                    } else {
                        // file.exist()当文件被删除时，只是被标记为删除，直到所有的句柄关闭，才返回false，否则为true。所以用这个方法判断文件是否存在不准确
                        if (events.size() == 0) {
                            // 根文件夹被删除
                            onFolderDelete(folder);
                        } else {
                            for (WatchEvent event : events) {
                                Path context = (Path) event.context();
                                // filename是当前监控的文件夹的直接文件名或文件夹名，不能获取到子文件夹里的filename
                                String filename = context.getFileName().toString();
                                if (isInvalidEvent(filename)) {
                                    continue;
                                }
                                File file = new File(folder.getAbsolutePath() + File.separator + filename);
                                WatchEvent.Kind kind = event.kind();// modify、create、delete、overflow
                                switch (kind.name()) {
                                    case WatchEventEnum.CREATE:
                                        if (file.isFile()) {
                                            onFileCreate(file);
                                        } else {
                                            onFolderCreate(file);
                                        }
                                        break;
                                    case WatchEventEnum.MODIFY:
                                        if (file.isFile()) {
                                            onFileModify(file);
                                        }
                                        break;
                                    case WatchEventEnum.DELETE:
                                        if (isFolder(file.getAbsolutePath())) {
                                            // 自身删除事件会先触发events=0的事件，父监听器会在触发一次
                                            onFolderDelete(file);
                                        } else {
                                            onFileDelete(file);
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
            subFolders.clear();
            watcher.close();
            monitor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------- 私有方法 ----------------------------------


    private void onFileRename(String oldPath, String newPath) {
        listener.onFileRename(oldPath, newPath);
    }


    private void onFileDelete(File file) {
        if (!file.exists()) {
            listener.onFileDelete(file);
        }
    }

    private void onFileCreate(File file) {
        /*
         - 修改已存在文件时,会产生modify、create事件,需过滤create
         - 如果是复制,会产生create、modify事件，复制由modify处理
        */
        if (file.isFile()) {
            listener.onFileCreate(file);
            if (file.length() > 0) {
                modifyEventCache.put(file.getAbsolutePath(), file);
                listener.onFileModify(file);
            }
        }
    }

    private void onFileModify(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                // 修改时，会触发2次修改事件
                File oldFile = modifyEventCache.getIfPresent(file.getAbsolutePath());
                // 同一个事件在2s内没有处理过（不为null，表示在2s内处理过，不进行处理）
                if (oldFile == null) {
                    // 标识已处理该事件
                    modifyEventCache.put(file.getAbsolutePath(), file);
                    // 判断文件内容是否为：有效修改
                    if (file.length() > 0) {
                        // 缓存有效文件名称
                        validFiles.add(file.getAbsolutePath());
                        // 通知修改内容
                        listener.onFileModify(file);
                    }
                    // 文件内容为空，可能是全删除，也可能是复制空文件（复制的空文件不触发modify，全删除内容则触发）
                    else {
                        // 文件之前有内容，现在全删除了
                        if (validFiles.contains(file.getAbsolutePath())) {
                            // 通知修改
                            listener.onFileModify(file);
                        } else {
                            // 标识该文件为无效文件
                            validFiles.remove(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    private void onFolderRename(String oldPath, String newPath) {
        subFolders.remove(oldPath);
        subFolders.add(newPath);
        FolderMonitor oldMonitor = monitors.remove(oldPath);
        if (oldMonitor != null) {
            try {
                FolderMonitor newMonitor = get(new File(newPath), oldMonitor.listener);
                if (oldMonitor.started) {
                    oldMonitor.stop();
                    newMonitor.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        listener.onFolderRename(oldPath, newPath);
    }

    private void onFolderCreate(File folder) throws Exception {
        subFolders.add(folder.getAbsolutePath());
        listener.onFolderCreate(folder);
    }


    private void onFolderDelete(File file) {
        if (isFolder(file.getAbsolutePath())) {
            subFolders.remove(file.getAbsolutePath());
            FolderMonitor monitor = monitors.remove(file.getAbsolutePath());
            if (monitor != null) {
                if (monitor.started) {
                    monitor.stop();
                }
                listener.onFolderDelete(file);
            }
        }
    }

    private void initFolderMonitor() {
        // 获取下级文件夹，没有递归获取
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        for (File file : files) {
            subFolders.add(file.getAbsolutePath());
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

    private String getPathFromEvent(WatchEvent<?> event) {
        Path context = (Path) event.context();
        String filename = context.getFileName().toString();
        return this.folder + File.separator + filename;
    }

    private boolean isFolder(String path) {
        if (subFolders.contains(path)) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(FolderMonitor o) {
        return folder.compareTo(o.folder);
    }

    private static class WatchEventEnum {

        private static final String MODIFY = "ENTRY_MODIFY";
        private static final String CREATE = "ENTRY_CREATE";
        private static final String DELETE = "ENTRY_DELETE";
    }

}
