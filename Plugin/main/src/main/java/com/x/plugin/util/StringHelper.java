package com.x.plugin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author AD
 * @date 2022/5/13 15:28
 */
public class StringHelper {

    public static boolean isAllNotNull(String... ss) {
        for (String s : ss) {
            if (isNull(s)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotNull(String s) {
        return !isNull(s);
    }

    public static boolean isNull(String s) {
        return s == null || "".equals(s.trim()) || "null".equalsIgnoreCase(s.trim());
    }

    public static String toString(Object o) {
        if (o == null || isNull(o.toString())) {
            return "";
        }
        return o.toString().trim();
    }

    public static String firstToUpperCase(String s) {
        byte[] bs = s.getBytes();
        bs[0] = (byte) ((bs[0]) - 'a' + 'A');
        return new String(bs);
    }

    public static String firstToLowerCase(String s) {
        byte[] bs = s.getBytes();
        bs[0] = (byte) ((bs[0]) - 'A' + 'a');
        return new String(bs);
    }

    public static String getExceptionTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 字节转换为B KB MB GB
     * @param size 字节⼤⼩
     * @return
     */
    public static String getStringSize(long size) {
        long rest = 0;
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size /= 1024;
        }
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            rest = size % 1024;
            size /= 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((rest * 100 / 1024 % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }


}
