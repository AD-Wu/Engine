package com.x.isearch.server.es.data;

import com.x.isearch.server.es.analyzer.Analyzer;
import com.x.isearch.server.es.analyzer.CharFilter;
import com.x.isearch.server.es.analyzer.TokenFilter;
import com.x.isearch.server.es.analyzer.Tokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AD
 * @date 2022/2/23 12:24
 */
public class Setting {

    // -------------------------- 成员变量 --------------------------
    private int shards;
    private int replicas;
    private final Analyzer[] analyzers;
    /**
     * 属性(es默认都转成string,见Settings.builder)
     */
    private final Json properties;

    // -------------------------- 构造方法 --------------------------

    /**
     * 构造方法
     * @param analyzers 分析器
     */
    public Setting(Analyzer... analyzers) {
        List<Analyzer> as = new ArrayList<>();
        as.add(Analyzer.textAnalyzer());
        as.add(Analyzer.textSearchAnalyzer());
        as.add(Analyzer.completionAnalyzer());
        as.add(Analyzer.completionSearchAnalyzer());
        as.add(Analyzer.standardAnalyzer());
        as.add(Analyzer.standardSearchAnalyzer());
        if (analyzers != null && analyzers.length > 0) {
            for (Analyzer analyzer : analyzers) {
                as.add(analyzer);
            }
        }
        this.shards = 5;
        this.replicas = 1;
        this.analyzers = as.toArray(new Analyzer[0]);
        this.properties = new Json();
        init();
    }

    // -------------------------- 静态方法 --------------------------

    public static Setting defaultSetting() {
        // 默认创建文本分析器,自动补全分析器
        Setting setting = new Setting();
        for (Analyzer analyzer : setting.getAnalyzers()) {
            // 存入引用名
            String charFilterKey = "analysis.analyzer." + analyzer.getName() + ".char_filter";
            String tokenizerKey = "analysis.analyzer." + analyzer.getName() + ".tokenizer";
            String tokenFilterKey = "analysis.analyzer." + analyzer.getName() + ".filter";
            setting.addProperties(charFilterKey,
                                  Arrays.stream(analyzer.getCharFilters()).map(cf -> cf.getName()).collect(Collectors.toList()));
            setting.addProperties(tokenizerKey, analyzer.getTokenizer().getName());
            setting.addProperties(tokenFilterKey,
                                  Arrays.stream(analyzer.getTokenFilters()).map(tf -> tf.getName()).collect(Collectors.toList()));
            // 构建charFilter对象
            String charKey = "analysis.char_filter.";
            for (CharFilter charFilter : analyzer.getCharFilters()) {
                String key = charKey + charFilter.getName();
                if (charFilter.getProperties() != null && charFilter.getProperties().size() > 0) {
                    setting.addProperties(key, charFilter.getProperties());
                }
            }
            // 构建tokenizer对象(自定义时才有属性)
            String tokenKey = "analysis.tokenizer.";
            Tokenizer tokenizer = analyzer.getTokenizer();
            if (tokenizer.getProperties() != null && tokenizer.getProperties().size() > 0) {
                setting.addProperties(tokenKey + tokenizer.getName(), tokenizer.getProperties());
            }
            // 构建filter对象
            String filterKey = "analysis.filter.";
            for (TokenFilter tokenFilter : analyzer.getTokenFilters()) {
                String key = filterKey + tokenFilter.getName();
                if (tokenFilter.getProperties() != null && tokenFilter.getProperties().size() > 0) {
                    setting.addProperties(key, tokenFilter.getProperties());
                }
            }
        }
        return setting;
    }

    // -------------------------- 成员方法 --------------------------


    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }

    public Analyzer[] getAnalyzers() {
        return analyzers;
    }

    public Map<String, Object> getProperties() {
        return properties.get();
    }

    public Setting addProperties(String key, Object value) {
        properties.add(key, value);
        return this;
    }

    // -------------------------- 私有方法 --------------------------
    private void init() {
        // 主分片
        properties.add("number_of_shards", shards);
        // 副本分片
        properties.add("number_of_replicas", replicas);
        // 高亮允许的最大偏移
        properties.add("highlight.max_analyzed_offset", 4000000);
        // 默认分析器
        // properties.add("analysis.analyzer.default.type", Tokenizer.DEFAULT_TOKENIZER);
    }

}
