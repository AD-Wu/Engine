package com.x.doraemon;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.stream.Collectors;

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
    
    /**
     * 将第一个字符转为小写
     * @param s 需转换的字符串
     * @return
     */
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
    public static String format(String str, Object... args) {
        String replace = str.replace("{}", "%s");
        Formatter formatter = new Formatter();
        String s = formatter.format(replace, args).toString();
        return s;
    }
    
    /**
     * 将字符串进行拼接
     * @param split 分隔符
     * @param os    需要拼接的对象（String）
     * @return 如：1,2,3,null
     */
    public static String joining(String split, Collection<Object> os) {
        if (os == null || os.size() == 0) {
            return "";
        }
        split = split == null ? "" : split;
        return os.stream().map(o -> String.valueOf(o)).collect(Collectors.joining(split));
    }
    
    /**
     * 将字符串进行拼接
     * @param split 分隔符
     * @param os    需要拼接的对象（String）
     * @return 如：1,2,3,null
     */
    public static String joining(String split, Object... os) {
        return joining(split, Arrays.asList(os));
    }
    
    /**
     * 获取堆栈异常信息
     * @param e 可抛出的异常
     * @return
     */
    public static String getExceptionTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        sw.flush();
        return sw.toString();
        
    }
    
}
