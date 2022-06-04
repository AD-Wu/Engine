package com.x.isearch.server.data.config.writer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 18:16
 */
public class ESWriterConfig {

    /**
     * es数据源名
     */
    private String esSourceName;

    /**
     * 索引名(为空时默认为表名)
     */
    private String index;

    public String getEsSourceName() {
        return esSourceName;
    }

    public void setEsSourceName(String esSourceName) {
        this.esSourceName = esSourceName;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
