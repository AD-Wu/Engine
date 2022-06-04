package com.x.plugin.core;

import com.x.plugin.data.PluginConfig;

/**
 * @author AD
 * @date 2022/5/13 14:11
 */
public interface IPluginConfigGenerator {

    boolean isValid();

    PluginConfig getPluginConfig();

    String fixName(String name);
}
