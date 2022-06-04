package com.x.plugin.facotry;

import com.x.plugin.core.IPluginManager;
import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginData;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AD
 * @date 2022/5/14 10:34
 */
public abstract class BasePluginManager implements IPluginManager {

    protected final Map<String, PluginData> plugins;
    protected final Map<String, BeanData> beans;
    protected final Map<String, BeanData> classnameBeans;

    protected final String envId;

    protected BasePluginManager(String envId) {
        this.envId = envId;
        this.plugins = new ConcurrentHashMap<>();
        this.beans = new ConcurrentHashMap<>();
        this.classnameBeans = new ConcurrentHashMap<>();
    }

    @Override
    public String getEnvId() {
        return envId;
    }

    @Override
    public PluginData[] getPluginDatas() {
        return plugins.values().toArray(new PluginData[0]);
    }

    @Override
    public boolean containsBean(String name) {
        return beans.containsKey(name.trim());
    }

    @Override
    public BeanData getBeanData(String name) {
        return beans.get(name);
    }

    protected abstract void addPluginData(PluginData data);
}
