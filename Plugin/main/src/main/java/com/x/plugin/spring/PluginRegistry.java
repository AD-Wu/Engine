package com.x.plugin.spring;

import com.x.plugin.core.IPlugin;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/5/7 21:57
 */
public final class PluginRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PluginRegistry.class);
    private final IPlugin[] plugins;
    private final Map<String, List<IPlugin>> envPlugins;

    public PluginRegistry(IPlugin[] plugins) {
        Objects.requireNonNull(plugins, "plugins can't be null");
        this.plugins = plugins;
        this.envPlugins = Arrays.stream(plugins)
            .collect(Collectors.groupingBy(p -> p.envId(), LinkedHashMap::new, Collectors.toList()));
    }

    public void registerToJVM() throws Exception {
        int total = plugins.length;
        logger.info("插件总共【{}】个", total);
        if (total > 0) {
            Iterator<Entry<String, List<IPlugin>>> it = envPlugins.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, List<IPlugin>> next = it.next();
                String envId = next.getKey();
                List<IPlugin> plugins = next.getValue();
                for (int i = 0, c = plugins.size(); i < c; i++) {
                    IPlugin p = plugins.get(i);
                    if (!p.exist()) {
                        logger.info("\t【{}】环境第【{}】个插件【{}】不存在", p.envName(), i + 1, p.name());
                        throw new FileNotFoundException(p.envName() + "环境插件:【" + p.name() + "】不存在");
                    }
                    // 1.注册插件包
                    if (p.registerToJVM()) {
                        logger.info("\t【{}】环境第【{}】个插件【{}】注册到虚拟机成功", p.envName(), i + 1, p.name());
                    } else {
                        logger.info("\t【{}】环境第【{}】个插件【{}】注册到虚拟机失败", p.envName(), i + 1, p.name());
                        throw new Exception("【" + p.envName() + "】环境插件【" + p.name() + "】注册到虚拟机失败");
                    }
                }
            }
            logger.info("注册【{}】个插件到JVM", plugins.length);
            logger.info(PluginIniter.splitLine);
        }
    }

}
