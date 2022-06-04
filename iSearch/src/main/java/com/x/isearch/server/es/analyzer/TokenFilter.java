package com.x.isearch.server.es.analyzer;

import com.x.isearch.server.es.data.Json;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 将tokenizer输出的词条做进一步处理。例如大小写转换、同义词处理、拼音处理等。
 * 1.分词过滤器接收分词流，并可以添加、移除或更改分词。
 * 2.如:lowercase过滤器将所有令牌转换为小写;stop过滤器从令牌流中删除共同单词（停止词）;synonym(同义词过滤器)将同义词引入词流。
 * 3.不允许更改每个分词的位置或字符偏移量。
 * 4.分析器可能有零个或多个分词过滤器，它们按顺序应用。
 * @author AD
 * @date 2022/2/23 16:28
 */
public class TokenFilter {

    // -------------------------- 成员变量 --------------------------

    /**
     * 分词过滤器名
     */
    private final String name;
    /**
     * 字符过滤器其它属性(只读)
     */
    private final Json properties;

    // -------------------------- 构造方法 --------------------------

    public TokenFilter(String name) {
        this.name = name;
        this.properties = new Json();
    }

    public TokenFilter(Type type) {
        this.name = type.toString().toLowerCase();
        this.properties = new Json();
        if (type.getProperties() != null && type.getProperties().size() > 0) {
            type.getProperties().entrySet().forEach(e -> properties.add(e.getKey(), e.getValue()));
        }
    }

    // -------------------------- 静态方法 --------------------------

    public static TokenFilter[] defaultTokenFilters() {
        TokenFilter lowercase = new TokenFilter(Type.lowercase);
        TokenFilter asciifolding = new TokenFilter(Type.asciifolding);
        TokenFilter pinyin = new TokenFilter(Type.pinyin);
        return new TokenFilter[]{lowercase, asciifolding, pinyin};
    }

    public static TokenFilter[] defaultSearchTokenFilters() {
        TokenFilter lowercase = new TokenFilter(Type.lowercase);
        TokenFilter asciifolding = new TokenFilter(Type.asciifolding);
        return new TokenFilter[]{lowercase, asciifolding};
    }

    // -------------------------- 成员方法 --------------------------

    public TokenFilter addProperties(String key, Object value) {
        properties.add(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties.get();
    }

    // -------------------------- 内部类 --------------------------

    public static enum Type {
        pinyin {
            @Override
            public Map<String, Object> getProperties() {
                Map<String, Object> ps = new LinkedHashMap<>();
                ps.put("type", "pinyin");
                ps.put("keep_full_pinyin", false);
                ps.put("keep_joined_full_pinyin", true);
                ps.put("keep_original", true);
                ps.put("limit_first_letter_length", 20);
                ps.put("remove_duplicated_term", true);
                ps.put("none_chinese_pinyin_tokenize", false);
                return ps;
            }
        },
        lowercase {
            @Override
            public Map<String, Object> getProperties() {
                return null;
            }
        },
        asciifolding {
            @Override
            public Map<String, Object> getProperties() {
                return null;
            }
        };

        public abstract Map<String, Object> getProperties();
    }
}
