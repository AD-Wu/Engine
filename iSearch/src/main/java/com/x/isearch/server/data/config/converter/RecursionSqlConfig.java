package com.x.isearch.server.data.config.converter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 17:43
 */
public class RecursionSqlConfig {

    /**
     * 数据源名
     */
    private String dataSourceName;
    /**
     * 表名
     */
    private String table;
    /**
     * 父列名
     */
    private String supColumn;
    /**
     * 子列名
     */
    private String subColumn;
    /**
     * 填充子列值所在的列名
     */
    private String subValueColumn;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSupColumn() {
        return supColumn;
    }

    public void setSupColumn(String supColumn) {
        this.supColumn = supColumn;
    }

    public String getSubColumn() {
        return subColumn;
    }

    public void setSubColumn(String subColumn) {
        this.subColumn = subColumn;
    }

    public String getSubValueColumn() {
        return subValueColumn;
    }

    public void setSubValueColumn(String subValueColumn) {
        this.subValueColumn = subValueColumn;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
