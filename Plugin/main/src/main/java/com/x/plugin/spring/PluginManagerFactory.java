package com.x.plugin.spring;

import com.x.plugin.core.IBeanNameModifier;
import com.x.plugin.core.IPluginManager;
import com.x.plugin.facotry.BeanNameModifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * @author AD
 * @date 2022/5/14 11:47
 */
public final class  PluginManagerFactory {

    public static final IBeanNameModifier beanNameModifier = new BeanNameModifier();
    private static final Map<String, IPluginManager> managers = new ConcurrentHashMap<>();
    private static final Set<String> envIds = new HashSet<>();
    private static final Map<String, BeanDefinition> defs = new LinkedHashMap<>();

    private PluginManagerFactory() {}

    // -------------------------------- 公共方法 --------------------------------

    public static Set<String> getEnvIds() {
        return Collections.unmodifiableSet(envIds);
    }

    public static boolean containsEnvId(String envId) {
        return envIds.contains(envId.trim());
    }

    public static IPluginManager getPluginManager(String envId) {
        return managers.get(envId.trim());
    }

    // -------------------------------- 缺省方法 --------------------------------

    static IPluginManager[] getPluginManagers() {
        return managers.values().toArray(new IPluginManager[0]);
    }

    static void putPluginManager(IPluginManager manager) {
        envIds.add(manager.getEnvId());
        managers.put(manager.getEnvId(), manager);
    }

    static void putBeanDefinition(String name, BeanDefinition def) throws Exception {
        if (defs.containsKey(name)) {
            throw new BeanDefinitionStoreException("已存在Bean:" + name);
        }
        defs.put(name, def);
    }

    static Map<String, BeanDefinition> getBeanDefinitions() {
        return Collections.unmodifiableMap(defs);
    }


}
