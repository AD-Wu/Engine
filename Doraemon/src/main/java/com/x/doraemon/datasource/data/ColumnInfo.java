package com.x.doraemon.datasource.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 数据库列信息
 *
 * @author AD
 * @date 2022/1/13 11:32
 */
public class ColumnInfo {

    /**
     * 列名
     */
    private String name;
    /**
     * 类型
     */
    private int type;
    /**
     * 类型名
     */
    private String typeName;

    /**
     * 对应的Java类型名,如:java.lang.String、java.lang.Integer
     */
    private String className;

    /**
     * 是否可为null
     */
    private boolean nullable;
    /**
     * 是否主键
     */
    private boolean primaryKey;
    /**
     * 主键是否自增
     */
    private boolean autoIncrement;

    /**
     * 默认值
     */
    private Object defaultValue;
    /**
     * 列注释信息
     */
    private String comment;


    // -------------------------- set and get --------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
