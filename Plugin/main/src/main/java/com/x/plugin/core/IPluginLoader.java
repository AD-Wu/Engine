package com.x.plugin.core;

/**
 * @author AD
 * @date 2022/4/25 20:03
 */
public interface IPluginLoader {

    IPlugin[] getPlugins() throws Exception;
}
