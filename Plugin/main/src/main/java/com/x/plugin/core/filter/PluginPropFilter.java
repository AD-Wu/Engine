package com.x.plugin.core.filter;

import com.x.plugin.enums.PluginProperties;
import java.util.jar.JarEntry;

/**
 * @author AD
 * @date 2022/5/17 11:13
 */
public class PluginPropFilter implements IFilter<JarEntry> {

    @Override
    public boolean accept(JarEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(PluginProperties.filename);
    }
}
