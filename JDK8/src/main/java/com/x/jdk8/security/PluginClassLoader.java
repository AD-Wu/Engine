package com.x.jdk8.security;

/**
 * @author AD
 * @date 2022/6/4 16:42
 */
public class PluginClassLoader extends ClassLoader {
    
    /**
     * 加载器名
     */
    private String name;
    
    /**
     * 父类加载器
     */
    private ClassLoader parent;
    
    public PluginClassLoader() {
        this("Plugin", null);
    }
    
    public PluginClassLoader(String name, ClassLoader parent) {
        this.parent = parent;
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            return findClass(name);
        }
    }
    
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // 从父类中找
        Class<?> clazz = findFromParent(className);
        if (clazz != null) {
            return clazz;
        }
        // 从已加载的当中找
        clazz = findLoadedClass(className);
        if (clazz != null) {
            return clazz;
        }
        // 从本地缓存找
        clazz = findFromLocal(className);
        if (clazz != null) {
            return clazz;
        }
        throw new ClassNotFoundException("ClassLoader[" + name + "]:" + className);
    }
    
    private Class<?> findFromLocal(String name) throws ClassCastException {
        return null;
    }
    
    private Class<?> findFromParent(String name) throws ClassNotFoundException {
        Class<?> clazz = parent.loadClass(name);
        if (clazz != null) {
            return clazz;
        }
        return null;
    }
    
}
