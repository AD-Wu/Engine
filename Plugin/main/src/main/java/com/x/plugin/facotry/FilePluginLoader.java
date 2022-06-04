package com.x.plugin.facotry;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.IPluginLoader;
import com.x.plugin.core.PluginChecker;
import com.x.plugin.core.listener.IListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author AD
 * @date 2022/4/25 20:05
 */
public class FilePluginLoader implements IPluginLoader {

    /**
     * jar路径.文件夹或jar绝对路径
     */
    private final File[] paths;

    public FilePluginLoader(File... paths) {
        this.paths = paths;
    }

    @Override
    public IPlugin[] getPlugins() throws Exception {
        List<IPlugin> plugins = new ArrayList<>();
        if (paths != null && paths.length > 0) {
            for (File jarPath : paths) {
                if (jarPath.exists()) {
                    Path[] subs = Files.walk(Paths.get(jarPath.getAbsolutePath())).toArray(Path[]::new);
                    for (Path sub : subs) {
                        URL url = sub.toUri().toURL();
                        PluginChecker.checkFileUrl(url, new IListener<Properties>() {
                            @Override
                            public void onAccept(Properties prop) throws Exception {
                                FilePlugin plugin = new FilePlugin(sub.toFile(), prop);
                                plugins.add(plugin);
                            }
                        });
                    }
                }
            }
        }
        return plugins.toArray(new IPlugin[0]);
    }
}
