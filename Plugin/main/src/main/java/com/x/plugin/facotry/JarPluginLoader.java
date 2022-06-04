package com.x.plugin.facotry;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.IPluginLoader;
import com.x.plugin.core.PluginChecker;
import com.x.plugin.core.listener.IListener;
import com.x.plugin.enums.PluginProperties;
import com.x.plugin.util.StringHelper;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * 从Resource/plugins下加载插件(Jar中Jar)
 * @author AD
 * @date 2022/5/16 9:49
 */
public final class JarPluginLoader implements IPluginLoader {

    private static final Logger LOG = LoggerFactory.getLogger(JarPluginLoader.class);

    private final Path path;

    public JarPluginLoader() {
        this.path = Paths.get(PluginProperties.pluginsPath);
    }


    @Override
    public IPlugin[] getPlugins() throws Exception {
        ClassPathResource src = new ClassPathResource(path.toString());
        URL rootUrl = src.getURL();
        List<IPlugin> plugins = new ArrayList<>();
        if (rootUrl != null) {
            if (PluginChecker.jarUrl.accept(rootUrl)) {
                JarURLConnection conn = (JarURLConnection) rootUrl.openConnection();
                // 获取jar(即jarAbsPath所对应的jar包对象)
                try (JarFile jarFile = conn.getJarFile()) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (isValid(entry)) {
                            // 拷贝到app同级目录
                            try (InputStream in = jarFile.getInputStream(entry)) {
                                // 获取当前app所在目录
                                Path appPath = Paths.get("");
                                Path target = appPath.resolve(getDir(entry));
                                Path file = Paths.get(target.toAbsolutePath().toString(), getName(entry));

                                Files.deleteIfExists(file);
                                Files.createDirectories(target);
                                Files.copy(in, file);
                                // 读取新路径的插件
                               URL url = file.toUri().toURL();
                                PluginChecker.checkFileUrl(file.toUri().toURL(), new IListener<Properties>() {
                                    @Override
                                    public void onAccept(Properties prop) throws Exception {
                                        IPlugin plugin = new FilePlugin(file.toFile(), prop);
                                        plugins.add(plugin);
                                    }
                                });
                            }

                        }
                    }
                }
            }
        }
        return plugins.toArray(new IPlugin[0]);
    }


    private String getDir(JarEntry entry) {
        String name = entry.getName();
        int end = name.lastIndexOf("/");
        return name.substring(0, end);
    }

    private String getName(JarEntry entry) {
        String name = entry.getName();
        int start = name.lastIndexOf("/") + 1;
        return name.substring(start);
    }

    private boolean isValid(JarEntry entry) {
        return PluginChecker.endJar.accept(entry.getName()) && entry.getName().startsWith(path.toString());
    }


    private void copyJar() {

    }

    /**
     * 将路径修改为相对路径,resource下禁止绝对路径
     * @param path 需修正的路径
     * @return 修正后的路径, 如: a/b/c 或 a/b/c/ 或 a/b/c/file.xxx
     */
    private String fixPath(String path) {
        if (StringHelper.isNotNull(path)) {
            path = path.replace("\\\\", "/").replace("\\", "/");
            char[] chars = path.toCharArray();
            int index = 0;
            for (char c : chars) {
                if (c == '/') {
                    index++;
                } else {
                    break;
                }
            }
            return path.substring(index);
        }
        return path;
    }

}
