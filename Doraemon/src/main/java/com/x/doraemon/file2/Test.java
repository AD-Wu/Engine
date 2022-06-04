package com.x.doraemon.file2;

import java.nio.file.Paths;

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
        String dir = "C:\\Users\\chunquanw\\Desktop\\文件夹监控测试1";
        IFileListener listener = new RecurseListener();
        FolderMonitor monitor = FolderMonitor.get(Paths.get(dir), listener);
        monitor.start();

    }

}
