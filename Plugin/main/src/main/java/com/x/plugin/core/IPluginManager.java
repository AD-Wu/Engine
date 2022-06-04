package com.x.plugin.core;

import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginData;

/**
 * @author AD
 * @date 2022/5/14 10:24
 */
public interface IPluginManager {

    String getEnvId();

    PluginData[] getPluginDatas();

    boolean containsBean(String name);

    BeanData getBeanData(String name);

    Object getBean(String name);

    Object getBean(String name, Object... args);

    <T> T getBean(String name, Class<T> clazz);

    <T> T getBean(Class<T> clazz);

    <T> T getBean(Class<T> clazz, Object... args);


}
