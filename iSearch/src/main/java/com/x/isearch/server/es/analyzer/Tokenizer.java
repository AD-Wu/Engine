package com.x.isearch.server.es.analyzer;

import com.x.isearch.server.es.data.Json;
import java.util.Map;

/**
 * 将文本按照一定的规则切割成词条（term）。例如keyword，就是不分词
 * 1.分词器接收字符流，将其分解为单个标记（通常是单个单词），然后输出一个标记流.
 * 2.如:whitespace分词器当遇到空白格时会将文本进行分解."Quick brown fox!" >> [Quick, brown, fox!].
 * 3.分词器还负责记录每个术语的顺序或位置，以及该术语所代表的原始单词的起始和结束字符偏移量.
 * 4.分析器有且只有一个分词器.
 * @author AD
 * @date 2022/2/23 15:41
 */
public class Tokenizer {

    public static final String DEFAULT_TOKENIZER = Type.STANDARD.toString().toLowerCase();
    // -------------------------- 成员变量 --------------------------

    /**
     * 分词器名
     */
    private final String name;
    /**
     * 分词器器其它属性(只读)
     */
    private final Json properties;

    // -------------------------- 构造方法 --------------------------

    /**
     * 私有构造方法
     * @param name 分词器名称
     */
    public Tokenizer(String name) {
        this.name = name.toLowerCase();
        this.properties = new Json();
    }

    /**
     * 私有构造方法
     * @param type 分词器类型
     */
    public Tokenizer(Type type) {
        this(type.toString());
    }

    // -------------------------- 静态方法 --------------------------

    /**
     * 文本分词器(ik_MAX_WORD)
     * @return
     */
    public static Tokenizer textTokenizer() {
        return new Tokenizer(Type.IK_MAX_WORD);
    }

    /**
     * 标准分词器(standard),即中文单字分词
     * @return
     */
    public static Tokenizer standardTokenizer() {
        return new Tokenizer(Type.STANDARD);
    }

    /**
     * 自动补全分词器
     * @return
     */
    public static Tokenizer completionTokenizer() {
        return new Tokenizer(Type.KEYWORD);
    }

    // -------------------------- 成员方法 --------------------------

    public Tokenizer addProperties(String key, Object value) {
        properties.add(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties.get();
    }

    public static enum Type {
        /**
         * ik智能分词
         */
        IK_SMART,
        /**
         * ik最大分词
         */
        IK_MAX_WORD,
        /**
         * es默认分词,英文分词器(中文会分割成单字)
         */
        STANDARD,
        /**
         * 用于自动补全
         */
        KEYWORD,

        // ------------------------- 不常用 -------------------------
        SIMPLE,
        WHITESPACE,
        STOP,
        PATTERN,
        LANGUAGE,
        FINGERPRINT;
    }
}
