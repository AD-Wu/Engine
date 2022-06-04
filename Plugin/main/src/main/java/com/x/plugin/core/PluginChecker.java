package com.x.plugin.core;

import com.x.plugin.core.filter.EndJarFilter;
import com.x.plugin.core.filter.JarFileFilter;
import com.x.plugin.core.filter.JarUrlFilter;
import com.x.plugin.core.filter.PluginPropFilter;
import com.x.plugin.core.filter.PropFilter;
import com.x.plugin.core.listener.IListener;
import com.x.plugin.util.UrlHelper;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/5/17 11:15
 */
public final class PluginChecker {

    private static final Logger LOG = LoggerFactory.getLogger(PluginChecker.class);

    public static final EndJarFilter endJar = new EndJarFilter();
    public static final JarFileFilter jarFile = new JarFileFilter();
    public static final JarUrlFilter jarUrl = new JarUrlFilter();
    public static final PluginPropFilter pluginProp = new PluginPropFilter();
    public static final PropFilter prop = new PropFilter();

    private PluginChecker() {}

    public static void checkFileUrl(URL url, IListener<Properties> propListener) throws Exception {
        if (url != null) {
            if (jarFile.accept(url) && endJar.accept(url.getFile())) {
                URL jarUrl = UrlHelper.toJarURL(new File(url.getFile()));
                readJar(jarUrl, propListener);
            }
        }
    }

    public static void checkJarUrl(URL url, IListener<Properties> propListener) throws Exception {
        if (url != null) {
            if (jarUrl.accept(url)) {
                readJar(url, propListener);
            }
        }
    }

    private static void readJar(URL url, IListener<Properties> listener) throws Exception {
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        if (conn != null) {
            try (JarFile jar = conn.getJarFile()) {
                if (jar != null) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (pluginProp.accept(entry)) {
                            try (InputStream in = jar.getInputStream(entry);
                                 InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                                Properties prop = new Properties();
                                prop.load(reader);
                                if (PluginChecker.prop.accept(prop)) {
                                    listener.onAccept(prop);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
