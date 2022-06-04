package com.x.isearch.server.es.analyzer;

/**
 * 文本分析由分析器执行,分析器是一组控制整个过程的规则.不同的语言需要有不同的分析器
 * 【分析器包括】:
 * 1、字符过滤器(character filter)
 * 2、分词器(tokenizer)
 * 3、分词过滤器(token filter,对分词结果进行加工,如:大小写转换,字符转换,中文转拼音等)
 * 【文本分析分两次进行】:
 * 1.索引时间:为文档编制索引时,字段为text属性的将被分析。
 * 2.搜索时间:在字段属性为text上运行全文搜索时,查询的字符串将被分析(用户正在搜索的文本)。
 * 【分析器的优先级】:搜索时指定 > 字段指定 > 索引指定
 * @author AD
 * @date 2022/2/19 18:29
 */
public class Analyzer {

    // -------------------------- 常量 --------------------------

    /**
     * 文本分析器名称(创建索引时用,主要包含拼音过滤器)
     */
    public static final String TEXT_ANALYZER = "text_analyzer";

    /**
     * 文本分析器名称(创建索引时用,不包含拼音过滤器,避免同音字)
     */
    public static final String TEXT_SEARCH_ANALYZER = "text_search_analyzer";

    /**
     * 标准分析器名称(创建索引时用,主要包含拼音过滤器),即中文单字分词
     */
    public static final String STANDARD_ANALYZER = "standard_analyzer";

    /**
     * 标准分析器名称(创建索引时用,不包含拼音过滤器,避免同音字),即中文单字分词
     */
    public static final String STANDARD_SEARCH_ANALYZER = "standard_search_analyzer";

    /**
     * 自动补全分析器名称(创建索引时用,包含拼音过滤器)
     */
    public static final String COMPLETION_ANALYZER = "completion_analyzer";


    /**
     * 自动补全分析器名称(搜索时用,不包含拼音过滤器,避免同音字)
     */
    public static final String COMPLETION_SEARCH_ANALYZER = "completion_search_analyzer";

    // -------------------------- 成员变量 --------------------------

    /**
     * 分析器名字
     */
    private final String name;

    /**
     * 分词器默认类型standard
     */
    private String defaultType;
    /**
     * 字符过滤器
     */
    private CharFilter[] CharFilters;
    /**
     * 分词器
     */
    private Tokenizer tokenizer;
    /**
     * 分词过滤器(对分词后的结果进行加工改造)
     */
    private TokenFilter[] TokenFilters;

    // -------------------------- 构造方法 --------------------------

    /**
     * 私有构造方法
     * @param name 分析器名称
     */
    private Analyzer(String name) {
        this.name = name;
        this.defaultType = Tokenizer.Type.STANDARD.toString().toLowerCase();
    }

    // -------------------------- 静态方法 --------------------------

    public static Analyzer newAnalyzer(String name, Tokenizer tokenizer) {
        // 创建分析器
        Analyzer analyzer = new Analyzer(name);
        // 字符过滤器
        analyzer.setCharFilters(CharFilter.defaultCharFilters());
        // 分词器
        analyzer.setTokenizer(tokenizer);
        // 分词过滤器
        analyzer.setTokenFilters(TokenFilter.defaultTokenFilters());
        return analyzer;
    }

    public static Analyzer newSearchAnalyzer(String name, Tokenizer tokenizer) {
        // 创建分析器
        Analyzer analyzer = new Analyzer(name);
        // 字符过滤器
        analyzer.setCharFilters(CharFilter.defaultCharFilters());
        // 分词器
        analyzer.setTokenizer(tokenizer);
        // 分词过滤器(不包括pinyin,搜索时不分解成拼音,避免同音字)
        analyzer.setTokenFilters(TokenFilter.defaultSearchTokenFilters());
        return analyzer;
    }

    public static Analyzer textAnalyzer() {
        return newAnalyzer(TEXT_ANALYZER, Tokenizer.textTokenizer());
    }

    public static Analyzer textSearchAnalyzer() {
        return newSearchAnalyzer(TEXT_SEARCH_ANALYZER, Tokenizer.textTokenizer());
    }

    public static Analyzer standardAnalyzer() {
        return newAnalyzer(STANDARD_ANALYZER, Tokenizer.standardTokenizer());
    }

    public static Analyzer standardSearchAnalyzer() {
        return newSearchAnalyzer(STANDARD_SEARCH_ANALYZER, Tokenizer.standardTokenizer());
    }

    public static Analyzer completionAnalyzer() {
        return newAnalyzer(COMPLETION_ANALYZER, Tokenizer.completionTokenizer());
    }

    public static Analyzer completionSearchAnalyzer() {
        return newSearchAnalyzer(COMPLETION_SEARCH_ANALYZER, Tokenizer.completionTokenizer());
    }

    // -------------------------- 成员方法 --------------------------

    public String getName() {
        return name;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public Analyzer setDefaultType(String defaultType) {
        this.defaultType = defaultType;
        return this;
    }

    public CharFilter[] getCharFilters() {
        return CharFilters;
    }

    public Analyzer setCharFilters(CharFilter... charFilters) {
        CharFilters = charFilters;
        return this;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public Analyzer setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public TokenFilter[] getTokenFilters() {
        return TokenFilters;
    }

    public Analyzer setTokenFilters(TokenFilter... tokenFilters) {
        TokenFilters = tokenFilters;
        return this;
    }

}
