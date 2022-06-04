package com.x.isearch.server.es.data;

import com.x.doraemon.Arrayx;
import com.x.doraemon.Converts;
import com.x.isearch.server.es.analyzer.Analyzer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AD
 * @date 2022/2/19 18:28
 */
public final class Mapping {

    // -------------------------- 常量 --------------------------

    public static final String SIMPLE_SEARCH_SUFFIX = "-simple";
    public static final String COMPLETION_SEARCH_SUFFIX = "-completion";

    // -------------------------- 成员变量 --------------------------

    /**
     * 字段名
     */
    private final String column;
    /**
     * 字段类型
     */
    private final ESType type;
    /**
     * 是否存储,默认:false
     */
    private boolean store;
    /**
     * 权重,默认:1
     */
    private int boost;
    /**
     * 1.往索引写入文档时所用的分词器(可能自定义)
     * 2.在大多数情况下，索引和搜索时应使用相同的分析器。这可确保字段的值和查询字符串更改为相同形式的标记。反过来，这将确保分词在搜索过程中与预期匹配。
     */
    private String analyzer;
    /**
     * 搜索时所用的分析器(指定search_analyzer时,必须指定analyzer)
     */
    private String searchAnalyzer;

    /**
     * 属性,如:type,boost,store,format,analyzer等
     */
    private Json properties;

    // -------------------------- 构造方法 --------------------------

    /**
     * 构造方法
     * @param column 字段名,统一转成小写连字符
     * @param type   字段类型
     */
    private Mapping(String column, ESType type) {
        this(column, type, false);
    }

    /**
     * 构造方法
     * @param column 字段名,统一转成小写连字符
     * @param type   字段类型
     */
    private Mapping(String column, ESType type, boolean innerProperties) {
        this.column = column;
        // this.column = formatColumn(column);
        this.type = type;
        this.properties = new Json();
        properties.add("type", type.toString().toLowerCase());
        // 非嵌套型数据
        if (!innerProperties) {
            this.store = false;
            this.boost = 1;
            properties.add("store", store);
            properties.add("boost", boost);
        }
    }
    // -------------------------- 静态方法 --------------------------

    public static Mapping newInteger(String column) {
        Mapping mapping = new Mapping(column, ESType.INTEGER);
        return mapping;
    }

    public static Mapping newLong(String column) {
        Mapping mapping = new Mapping(column, ESType.LONG);
        return mapping;
    }

    public static Mapping newDouble(String column) {
        Mapping mapping = new Mapping(column, ESType.DOUBLE);
        return mapping;
    }

    public static Mapping newBoolean(String column) {
        Mapping mapping = new Mapping(column, ESType.BOOLEAN);
        return mapping;
    }

    public static Mapping newKeyword(String column) {
        Mapping mapping = new Mapping(column, ESType.KEYWORD);
        return mapping;
    }

    public static Mapping newDateTime(String column) {
        Mapping mapping = new Mapping(column, ESType.DATE);
        mapping.dateFormat();
        return mapping;
    }

    /**
     * 创建类型为text的mapping列,默认使用ik分词器
     * @param column
     * @param searchTypes
     * @return
     */
    public static List<Mapping> newText(String column, SearchType... searchTypes) {
        List<SearchType> types = new ArrayList<>();
        types.add(SearchType.TEXT);
        if (searchTypes != null && searchTypes.length > 0) {
            for (SearchType searchType : searchTypes) {
                types.add(searchType);
            }
        }
        List<Mapping> mappings = new ArrayList<>();
        types.stream().distinct().forEach(t -> {
            switch (t) {
                case TEXT:
                    Mapping mapping = new Mapping(column, ESType.TEXT);
                    mapping.setSearchAnalyzer(Analyzer.TEXT_SEARCH_ANALYZER, Analyzer.TEXT_ANALYZER);
                    mappings.add(mapping);
                    break;
                case SIMPLE:
                    Mapping simpMapping = new Mapping(column + SIMPLE_SEARCH_SUFFIX, ESType.TEXT);
                    simpMapping.setSearchAnalyzer(Analyzer.STANDARD_SEARCH_ANALYZER, Analyzer.STANDARD_ANALYZER);
                    mappings.add(simpMapping);
                    break;
                case COMPLETION:
                    Mapping compMapping = new Mapping(column + COMPLETION_SEARCH_SUFFIX, ESType.COMPLETION);
                    compMapping.setSearchAnalyzer(Analyzer.COMPLETION_SEARCH_ANALYZER, Analyzer.COMPLETION_ANALYZER);
                    compMapping.otherProperties("max_input_length", 100);
                    mappings.add(compMapping);
                    break;
                default:
                    break;
            }
        });
        return mappings;
    }

    /*
    PUT my-index-000001
    {
      "mappings": {
        "properties": {
          "engineer": {
            "type": "nested",
            "properties": {
              "age":  { "type": "integer" },
              "name": { "type": "text"  }
            }
          }
        }
      }
    }
     */
    public static Mapping newNested(String column, Mapping[] mappings) {
        Mapping mapping = new Mapping(column, ESType.NESTED, true);
        Map<String, Object> properties = new LinkedHashMap<>();
        for (Mapping m : mappings) {
            properties.put(m.getColumn(), m.getProperties());
        }
        mapping.otherProperties("properties", properties);
        return mapping;
    }

    /*
    PUT my-index-000001
    {
      "mappings": {
        "properties": {
          "region": {
            "type": "keyword"
          },
          "manager": {
            "properties": {
              "age":  { "type": "integer" },
              "name": {
                "properties": {
                  "first": { "type": "text" },
                  "last":  { "type": "text" }
                }
              }
            }
          }
        }
      }
    }
    */
    // public static Mapping newObject(String column, Mapping[] mappings) {
    //     Mapping mapping = new Mapping(column, ESType.OBJECT, true);
    //     Map<String, Object> properties = new LinkedHashMap<>();
    //     for (Mapping m : mappings) {
    //         properties.put(m.getColumn(), m.getProperties());
    //     }
    //     mapping.otherProperties("properties", properties);
    //     return mapping;
    // }


    public static String formatColumn(String column) {
        return Converts.toLowerHyphen(column);
    }

    // -------------------------- 成员方法 --------------------------
    public Mapping dateFormat(String... patterns) {
        StringBuilder sb = new StringBuilder();
        if (Arrayx.isNotEmpty(patterns)) {
            for (int i = 0, c = patterns.length; i < c; i++) {
                sb.append(patterns[i]).append("||");
            }
        }
        // 默认样式
        sb.append("yyyy-MM-dd HH:mm:ss").append("||");
        sb.append("yyyy-MM-dd").append("||");
        sb.append("yyyy/MM/dd HH:mm:ss").append("||");
        sb.append("yyyy/MM/dd").append("||");
        sb.append("HH:mm:ss.SSS").append("||");
        sb.append("HH:mm:ss").append("||");
        sb.append("epoch_millis");
        properties.add("format", sb.toString());
        return this;
    }


    public String getColumn() {
        return column;
    }

    public ESType getType() {
        return type;
    }

    public boolean isStore() {
        return store;
    }

    public void setStore(boolean store) {
        this.store = store;
    }

    public int getBoost() {
        return boost;
    }

    public void setBoost(int boost) {
        this.boost = boost;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
        properties.add("analyzer", analyzer);
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(String searchAnalyzer, String analyzer) {
        this.searchAnalyzer = searchAnalyzer;
        properties.add("search_analyzer", searchAnalyzer);
        properties.add("analyzer", analyzer);
    }

    public void otherProperties(String key, Object value) {
        this.properties.add(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties.get();
    }


}
