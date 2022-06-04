package com.x.isearch.server.data.config.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 17:44
 */
public class SqlConfig{
    /**
     * 数据源名,唯一标识
     */
    private String dataSourceName;
    /**
     * 需要执行的sql
     */
    private String sql;
    /**
     * 用于填充sql中"?"的列名(值来自上一个转换器里的map)
     */
    private String[] valueColumns;


    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String[] getValueColumns() {
        return valueColumns;
    }

    public void setValueColumns(String[] valueColumns) {
        this.valueColumns = valueColumns;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
