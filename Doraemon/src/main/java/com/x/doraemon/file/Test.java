package com.x.doraemon.file;

import java.io.File;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/12/7 12:01
 */
public class Test {

    public static void main(String[] args) throws Exception {
        testMonitor();
    }

    private static void testMonitor() throws Exception {
        IFileListener listener = new RecurseListener();
        FolderMonitor monitor = FolderMonitor.get(new File("C:\\Users\\chunquanw\\Desktop\\文件夹监控测试"), listener);
        monitor.start();

    }

}
