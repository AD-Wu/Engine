package com.x.doraemon;

import java.util.Formatter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author AD
 * @desc
 * @date 2022/1/10 1:16
 */
public class Strings extends StringUtils {

    // -------------------------- 构造方法 --------------------------

    /**
     * 构造方法
     */
    private Strings() {
    }

    // -------------------------- 静态方法 --------------------------

    /**
     * 判断字符串是否为空,"null"也为空(不区分大小写)
     * @param check 需校验字符串
     * @return
     */
    public static boolean isNull(String check) {
        return isNull(check, "null");
    }

    /**
     * 判断字符串是否为空
     * @param check 需校验字符串
     * @param nulls 为空的字符串,如:"null","string"
     * @return
     */
    public static boolean isNull(String check, String... nulls) {
        if (isBlank(check)) {
            return true;
        }
        if (nulls != null && nulls.length > 0) {
            for (int i = 0, c = nulls.length; i < c; i++) {
                if (check.equalsIgnoreCase(nulls[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String firstToLower(String s) {
        if (isBlank(s)) {
            return s;
        }
        byte[] bs = s.getBytes();
        bs[0] = (byte) (bs[0] - 65 + 97);
        return new String(bs);
    }

    /**
     * 替换占位符
     * @param str
     * @param args
     * @return
     */
    public static String replace(String str, Object... args) {
        String replace = str.replace("{}", "%s");
        Formatter formatter = new Formatter();
        String s = formatter.format(replace, args).toString();
        return s;
    }

}
