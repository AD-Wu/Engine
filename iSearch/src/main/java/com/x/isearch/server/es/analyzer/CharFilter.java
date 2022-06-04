package com.x.isearch.server.es.analyzer;

import com.x.isearch.server.es.data.Json;
import java.util.Map;

/**
 * 在tokenizer之前对文本进行处理。例如删除字符、替换字符。
 * 1.字符过滤器将原始文本作为字符流接收，并可以通过添加、删除或更改字符来转换流。
 * 2.例如，字符过滤器可用于转换印度教阿拉伯数字（٠‎١٢٣٤٥٦٧٨‎٩‎) 将其转换为阿拉伯语和拉丁语的等价物（0123456789），或从流中删除<b>等HTML元素。
 * 3.分析器(analyzer)可能有零个或多个字符过滤器，这些过滤器按顺序应用。
 * @author AD
 * @date 2022/2/23 15:42
 */
public class CharFilter {

    // -------------------------- 成员变量 --------------------------

    /**
     * 字符过滤器名
     */
    private final String name;
    /**
     * 字符过滤器其它属性(只读)
     */
    private final Json properties;

    // -------------------------- 构造方法 --------------------------

    private CharFilter(String name) {
        this.name = name;
        this.properties = new Json();
    }

    // -------------------------- 静态方法 --------------------------

    public static CharFilter[] defaultCharFilters() {
        CharFilter htmlStrip = new CharFilter(Type.html_strip.toString().toLowerCase());
        return new CharFilter[]{htmlStrip};
    }

    // -------------------------- 成员方法 --------------------------

    public CharFilter addProperties(String key, Object value) {
        properties.add(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties.get();
    }

    /**
     * 字符过滤器枚举
     */
    public static enum Type {
        /**
         * 过滤http标签
         */
        html_strip;
    }

}
