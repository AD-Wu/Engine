package com.x.plugin.facotry;

import com.x.plugin.core.IPlugin;
import com.x.plugin.enums.PluginProperties.Key;
import com.x.plugin.util.StringHelper;
import com.x.plugin.util.UrlHelper;
import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * @author AD
 * @date 2022/4/25 15:48
 */
public class FilePlugin implements IPlugin {

    private final File jar;

    private final URL url;

    private final Properties prop;

    private final String envId;

    private final String envName;

    private final String size;

    protected FilePlugin(File jar,Properties prop) throws Exception {
        this.jar = jar;
        this.url = UrlHelper.toJarURL(jar);
        this.size = StringHelper.getStringSize(jar.length());
        this.prop = prop;
        this.envId = prop.getProperty(Key.id.toString());
        this.envName = prop.getProperty(Key.name.toString());
    }


    @Override
    public boolean exist() {
        return jar.exists() && jar.isFile() && jar.getName().endsWith(".jar");
    }

    @Override
    public String envId() {
        return envId;
    }

    @Override
    public String envName() {
        return envName;
    }

    @Override
    public String name() {
        return jar.getName();
    }

    @Override
    public URL url() {
        return this.url;
    }

    @Override
    public String size() {
       return this.size;
    }

}
