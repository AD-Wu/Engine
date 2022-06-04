package com.x.isearch;

import com.x.doraemon.Printer;
import com.x.doraemon.common.interfaces.IFilter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * jar包工具类
 * @author AD
 * @date 2022/4/10 16:49
 */
public class JarLoader extends URLClassLoader {

    static final String jarPath = "D:\\GitCode\\Engine\\Doraemon\\target\\Doraemon-1.0.0.jar";

    public JarLoader(URL[] urls) {
        super(urls);
    }

    public static void main(String[] args) throws Exception {

        load();
    }

    public static void load() throws Exception {
        List<JarEntry> jarEntries = readJarEntry(jarPath, CLASS_FILTER);
        loadJar(jarPath);
        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL[] urls = loader.getURLs();
        // for (URL url : urls) {
        //     System.out.println(url);
        // }
        for (int i = 0, N = jarEntries.size(); i < N; i++) {
            JarEntry entry = jarEntries.get(i);
            String name = entry.getName();
            name = name.replace("/", ".");
            // int start = name.indexOf("com.x.doraemon");
            int end = name.lastIndexOf(".class");
            name = name.substring(0, end);
            Class<?> clz = loader.loadClass(name);
            System.out.println(clz);
            if (clz.getName().equals("com.x.doraemon.Reflects")) {
                Method m = clz.getDeclaredMethod("execute");
                boolean isStatic = Modifier.isStatic(m.getModifiers());
                m.getGenericParameterTypes();
                if (isStatic) {
                    m.invoke(null, null);
                } else {
                    Object o = null;
                    Constructor<?>[] cs = clz.getDeclaredConstructors();
                    for (Constructor<?> c : cs) {
                        c.setAccessible(true);
                        int count = c.getParameterCount();
                        if (count == 0) {
                            o = c.newInstance();
                        }
                    }
                    if (o != null) {
                        m.invoke(o, null);
                    }
                }
            }
        }
    }

    private static String jarURL = "jar:file:%s!/";

    public static List<JarEntry> readJarEntry(String jarAbsPath, IFilter<JarEntry> filter) throws Exception {
        Formatter fmt = new Formatter();
        String jarUrl = fmt.format(jarURL, jarAbsPath).toString();
        URL url = new URL(jarUrl);
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        if (conn != null) {
            // 获取jar(即jarAbsPath所对应的jar包对象)
            try (JarFile jarFile = conn.getJarFile();) {
                if (jarFile != null) {
                    // 获取jar包的内部结构(文件|文件夹都是JarEntry,文件也包括jar包)
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    List<JarEntry> entries = new ArrayList<>();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry entry = jarEntries.nextElement();
                        if (filter == null || filter.accept(entry)) {
                            entries.add(entry);
                        }
                    }
                    return entries;
                }
            }
        }
        return new ArrayList<>();
    }

    public static void loadJar(String jarAbsPath) throws Exception {
        File file = new File(jarAbsPath);

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            method.invoke(loader, file.toURI().toURL());
        } finally {
            method.setAccessible(accessible);
        }

    }

    public static final IFilter<JarEntry> JAR_FILTER = new IFilter<JarEntry>() {
        @Override
        public boolean accept(JarEntry entry) {
            return entry.getName().endsWith(".jar");
        }
    };

    public static final IFilter<JarEntry> CLASS_FILTER = new IFilter<JarEntry>() {
        @Override
        public boolean accept(JarEntry entry) {
            return entry.getName().endsWith(".class");
        }
    };

    public static final IFilter<JarEntry> FILE_FILTER = new IFilter<JarEntry>() {
        @Override
        public boolean accept(JarEntry entry) {
            return !entry.isDirectory();
        }
    };

    private static void printManifest(JarFile jarFile) throws IOException {
        Manifest manifest = jarFile.getManifest();
        if (manifest != null) {
            Attributes attrs = manifest.getMainAttributes();
            Printer printer = new Printer();
            for (Entry<Object, Object> e : attrs.entrySet()) {
                printer.add(e.getKey(), e.getValue());
            }
            printer.print();
        }
    }
}
