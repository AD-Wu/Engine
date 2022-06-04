package com.x.plugin.spring;

import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginData;
import com.x.plugin.facotry.BasePluginManager;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author AD
 * @date 2022/4/25 17:46
 */
public final class PluginManager extends BasePluginManager {

    private static ConfigurableListableBeanFactory beanFactory;

    PluginManager(String envId) {
        super(envId);
    }

    @Override
    public Object getBean(String name) {
        if (beans.containsKey(name)) {
            BeanData data = getBeanData(name);
            if (data.isModifyName()) {
                return beanFactory.getBean(modifyName(name));
            }
            return beanFactory.getBean(name);
        }
        return null;
    }

    @Override
    public Object getBean(String name, Object... args) {
        if (beans.containsKey(name.trim())) {
            BeanData data = getBeanData(name);
            if (data.isModifyName()) {
                return beanFactory.getBean(modifyName(name), args);
            }
            return beanFactory.getBean(name, args);
        }
        return null;

    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        if (beans.containsKey(name.trim())) {
            BeanData data = getBeanData(name);
            if (data.isModifyName()) {
                return beanFactory.getBean(modifyName(name), clazz);
            }
            return beanFactory.getBean(name, clazz);
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (classnameBeans.containsKey(clazz.getName())) {
            return beanFactory.getBean(clazz);
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz, Object... args) {
        if (classnameBeans.containsKey(clazz.getName())) {
            return beanFactory.getBean(clazz, args);
        }
        return null;
    }

    @Override
    protected synchronized void addPluginData(PluginData data) {
        if (!plugins.containsKey(data.getName())) {
            plugins.put(data.getName(), data);
            BeanData[] bds = data.getBeans();
            for (BeanData bd : bds) {
                beans.put(bd.getSrcName(), bd);
                classnameBeans.put(bd.getClassname(), bd);
            }
        }
    }

    private String modifyName(String name) {
        return PluginManagerFactory.beanNameModifier.modify(envId, name.trim());
    }

    static void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        PluginManager.beanFactory = beanFactory;
    }

}
