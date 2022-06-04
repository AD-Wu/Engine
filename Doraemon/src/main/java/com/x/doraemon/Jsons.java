package com.x.doraemon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.x.doraemon.DateTimes.Formatter;

/**
 * @author AD
 * @date 2022/2/22 19:12
 */
public class Jsons {

    // -------------------------- 构造方法 --------------------------

    /**
     * 构造方法
     */
    private Jsons() {}

    // -------------------------- 静态方法 --------------------------

    /**
     * 将对象解析成JSON字符串
     * @param src
     * @return
     */
    public static String toJson(Object src) {
        return toJson(src, Formatter.DEFAULT.getPattern());
    }

    /**
     * 将对象解析成JSON字符串
     * @param src
     * @param dateTimeFormatter
     * @return
     */
    public static String toJson(Object src, String dateTimeFormatter) {
        return gson(dateTimeFormatter).toJson(src);
    }

    /**
     * 将JSON字符串解析成对应的对象
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(json, clazz, Formatter.DEFAULT.getPattern());
    }

    /**
     * 将JSON字符串解析成对应的对象
     * @param json
     * @param clazz
     * @param dateTimeFormatter
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz, String dateTimeFormatter) {
        return gson(dateTimeFormatter).fromJson(json, clazz);
    }

    /**
     * 判断是否是有效的json字符串
     * @param check
     * @return
     */
    public static boolean valid(String check) {
        try {
            fromJson(check, Object.class, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Gson gson(String dateTimeFormatter) {
        if (Strings.isNull(dateTimeFormatter)) {
            return new Gson();
        } else {
            return new GsonBuilder().setDateFormat(dateTimeFormatter).create();
        }
    }

}
