package com.x.plugin.core;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author AD
 * @date 2022/4/25 17:48
 */
public interface IPlugin {

    boolean exist();

    String envId();

    String envName();

    /**
     * jar包名, 唯一
     * @return
     */
    String name();

    URL url();

    String size();

    default boolean registerToJVM() {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(Thread.currentThread().getContextClassLoader(), url());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
