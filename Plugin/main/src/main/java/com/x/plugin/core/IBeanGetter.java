package com.x.plugin.core;

import com.x.plugin.anno.RunPlugin;

/**
 * @author AD
 * @date 2022/5/24 17:53
 */
public interface IBeanGetter<T> {

    /**
     * 获取插件id
     * @return
     */
    String getPluginId(RunPlugin plugin, T pluginParam);

    /**
     * 获取Bean原始名称
     * @return
     */
    String getBeanName(RunPlugin plugin, T pluginParam);
}
