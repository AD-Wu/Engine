package com.x.doraemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author AD
 * @date 2022/4/20 22:17
 */
public class Zips {

    public static void zip(File src, File target) throws IOException {
        if (!src.exists()) {
            throw new FileNotFoundException(src.getAbsolutePath());
        }
        try (FileOutputStream fout = new FileOutputStream(target);
             ZipOutputStream zout = new ZipOutputStream(fout)) {
            if (src.isDirectory()) {
                zipMore(src, zout, src.getName());
            } else {
                zipOne(src, zout, src.getName());
            }
        }

    }

    public static void unzip() {

    }


    private static void zipMore(File src, ZipOutputStream out, String supName) throws IOException {
        out.putNextEntry(new ZipEntry(supName + "/"));
        File[] subs = src.listFiles();
        if (subs != null) {
            for (int i = 0, L = subs.length; i < L; i++) {
                File sub = subs[i];
                String subEntry = supName + "/" + sub.getName();
                if (sub.isDirectory()) {
                    zipMore(sub, out, subEntry);
                } else {
                    zipOne(sub, out, subEntry);
                }
            }
        }
    }

    private static void zipOne(File src, ZipOutputStream out, String entryName) throws IOException {
        // 写入entry
        out.putNextEntry(new ZipEntry(entryName));
        // 往entry里写入文件数据
        try (FileInputStream fin = new FileInputStream(src)) {
            byte[] buf = new byte[10240];
            int len = -1;
            while ((len = fin.read(buf)) != -1) {
                out.write(buf);
            }
            out.flush();
        }
    }

}
