package com.x.doraemon;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * @author AD
 * @date 2022/4/10 11:45
 */
public class Test {

    public static void main(String[] args) throws Exception {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        Printer p = new Printer();
        p.add("getBootClassPath", rb.getBootClassPath());
        p.add("getClassPath", rb.getClassPath());
        p.add("getLibraryPath", rb.getLibraryPath());
        p.add("getManagementSpecVersion", rb.getManagementSpecVersion());
        p.add("getName", rb.getName());
        p.add("getSpecName", rb.getSpecName());
        p.add("getSpecVendor", rb.getSpecVendor());
        p.add("getSpecVersion", rb.getSpecVersion());
        p.add("getStartTime", rb.getStartTime());
        p.add("getUptime", rb.getUptime());
        p.add("getVmName", rb.getVmName());
        p.add("getVmVendor", rb.getVmVendor());
        p.add("getVmVersion", rb.getVmVersion());
        p.print();
    }

    private static void c(){
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        String[] split = classPath.split(";");
        for (String s : split) {
            System.out.println(s);
        }

        System.out.println("----------------------");
        ClassLoader sourceClassLoader = Thread.currentThread().getContextClassLoader();
        if(sourceClassLoader instanceof URLClassLoader){
            URLClassLoader urlClassLoader = (URLClassLoader) sourceClassLoader;
            final URL[] urLs = urlClassLoader.getURLs();
            for (URL url : urLs) {
                System.out.println(url);
            }
        }
    }


    private static void b() throws MalformedURLException {
        File file = new File("");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.toURI().toURL());
    }

    private static void a()throws Exception{
        String path = "com/x";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> es = loader.getResources(path);
        if (es != null) {
            while (es.hasMoreElements()) {
                URL url = es.nextElement();
                print(url);
            }
        } else {
            URL[] urls = ((URLClassLoader) loader).getURLs();
            for (URL url : urls) {
                print(url);
            }
        }
    }

    private static void print(URL url) throws Exception {
        System.out.println("Protocol:" + url.getProtocol());
        System.out.println("Host:" + url.getHost());
        System.out.println("Port:" + url.getPort());
        System.out.println("DefaultPort:" + url.getDefaultPort());
        System.out.println("Authority:" + url.getAuthority());
        System.out.println("Path:" + url.getPath());
        System.out.println("Content:" + url.getContent());
        System.out.println("File:" + url.getFile());
        System.out.println("Ref:" + url.getRef());
        System.out.println("UserInfo:" + url.getUserInfo());
        System.out.println("---------------------------------");
    }
}
