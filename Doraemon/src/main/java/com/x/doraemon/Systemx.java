package com.x.doraemon;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import javax.swing.filechooser.FileSystemView;

/**
 * 系统帮助类
 * @author AD
 * @date 2022/2/23 17:47
 */
public class Systemx {


    public static void main(String[] args)  {
        int pid = getPid();
        System.out.println(pid);
    }

    private static void print() {
        System.out.println(getSystem());
        System.out.println(getLangKey());
        Properties properties = System.getProperties();
        properties.entrySet().forEach(p -> {
            System.out.println(p.getKey() + "=" + p.getValue());
        });
    }

    /**
     * 获取当前进程pid
     * @return 进程pid(-1表示获取失败)
     */
    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        // 12644@PCName
        String name = runtime.getName();
        int end = name.indexOf("@");
        int pid = -1;
        if (end != -1) {
            pid = Integer.parseInt(name.substring(0, end));
        }
        return pid;
    }

    /**
     * 获取系统字节的排序是大端还是小端
     * @return
     */
    public static ByteOrder getByteOrder() {
        /*
         1.Intel 处理器使用小端字节顺序。
         2.摩托罗拉的CPU系列、SUN的Sparc工作站使用大端字节顺序。
         */
        ByteOrder order = ByteOrder.nativeOrder();
        return order;
    }

    /**
     * 获取系统临时文件夹
     * @return
     */
    public static Path getTempFolder() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    /**
     * 获取系统Home路径,等同于:System.getProperty("user.home").
     * window:获取桌面路径
     * @return
     */
    public static Path getUserHome() {
        // System.getProperty("user.home")
        return FileSystemView.getFileSystemView().getHomeDirectory().toPath();
    }


    /**
     * 获取系统名称
     * @return
     */
    public static String getSystem() {
        return System.getProperty("os.name");
    }

    /**
     * 获取系统默认语言
     * @return 如：zh_CN
     */
    public static String getLangKey() {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage() + "_" + locale.getCountry();
        return lang;
    }
}
