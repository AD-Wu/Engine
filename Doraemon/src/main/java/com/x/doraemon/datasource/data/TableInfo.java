package com.x.doraemon.datasource.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author AD
 * @date 2022/1/13 11:57
 */
public class TableInfo {

    /**
     * 表名
     */
    private String name;

    /**
     * 列信息
     */
    private ColumnInfo[] columns;

    /**
     * 主键
     */
    private String[] primaryKeys;

    private Map<String, ColumnInfo> columnMap;

    public ColumnInfo getColumnInfo(String column) {
        return columnMap.get(column);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnInfo[] getColumns() {
        return columns;
    }

    public String[] getPrimaryKeys() {
        return primaryKeys;
    }

    public void setColumns(ColumnInfo[] columns) {
        this.columns = columns;
        this.columnMap = Arrays.stream(columns).collect(Collectors.toConcurrentMap(ColumnInfo::getName, c -> c));
        List<String> pks = new ArrayList<>();
        for (int i = 0, N = columns.length; i < N; i++) {
            ColumnInfo col = columns[i];
            if(col.isPrimaryKey()){
                pks.add(col.getName());
            }
        }
        this.primaryKeys = pks.toArray(new String[0]);
    }

    @Override
    public String toString() {
       return ToStringBuilder.reflectionToString(this);
    }
}
