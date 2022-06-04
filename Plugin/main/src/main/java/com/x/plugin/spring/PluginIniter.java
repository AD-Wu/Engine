package com.x.plugin.spring;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.IPluginConfigGenerator;
import com.x.plugin.facotry.FilePluginLoader;
import com.x.plugin.facotry.PluginConfigGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 * @author AD
 * @date 2022/4/26 15:45
 */
@Configuration
@Import(PluginIniter.class)
public class PluginIniter extends BasePluginIniter {

    @Override
    protected boolean isConfigFile(String configFileName) {
        return StringUtils.hasLength(configFileName) && (configFileName.startsWith("applicationConfig") || configFileName
            .contains("application") || configFileName.contains("bootstrap"));
    }

    @Override
    protected IPluginConfigGenerator getPluginConfigGenerator(Map<String, Object> config) {
        return new PluginConfigGenerator(config);
    }

    @Override
    protected List<IPlugin> getPlugins(String... jarPaths) throws Exception {
        List<IPlugin> plugins = new ArrayList<>();
        if (jarPaths != null && jarPaths.length > 0) {
            File[] files = Arrays.stream(jarPaths).map(path -> new File(path)).toArray(File[]::new);
            IPlugin[] ps = new FilePluginLoader(files).getPlugins();
            plugins.addAll(Arrays.asList(ps));
        }
        return plugins;
    }


}
