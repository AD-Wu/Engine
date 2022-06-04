package com.x.jdk8.security;

/**
 * @author AD
 * @date 2022/6/4 16:42
 */
public class PluginClassLoader extends ClassLoader{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }


}
