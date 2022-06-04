package com.x.plugin.facotry;

import com.x.plugin.core.IPlugin;
import com.x.plugin.enums.PluginProperties.Key;
import com.x.plugin.util.StringHelper;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author AD
 * @date 2022/5/16 19:41
 */
public final class JarPlugin implements IPlugin {

    private final URL jarUrl;
    private final String name;
    private final Properties prop;
    private final String envId;
    private final String envName;
    private long size;

    public JarPlugin(URL jarUrl, Properties prop) throws Exception {
        this.jarUrl = jarUrl;
        String url = jarUrl.toString();
        this.name = url.substring(url.lastIndexOf("/") + 1);
        this.prop = prop;
        this.envId = prop.getProperty(Key.id.toString());
        this.envName = prop.getProperty(Key.name.toString());
    }

    @Override
    public boolean exist() {
        return jarUrl != null;
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
        return name;
    }

    @Override
    public URL url() {
        return jarUrl;
    }

    @Override
    public String size() {
        if (size == 0) {
            synchronized (this) {
                if (size == 0) {
                    try (InputStream in = jarUrl.openStream()) {
                        size = in.available();
                        return StringHelper.getStringSize(size);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return StringHelper.getStringSize(this.size);
    }
}
