package com.x.isearch.server.data.config.converter;

import com.x.doraemon.reflect.DoraemonFunction;
import com.x.doraemon.reflect.method.Methods;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 18:14
 */
public class ConverterConfig {

    private final String className;

    private final Map<String, Object> params;

    public ConverterConfig(String className, Map<String, Object> params) {
        this.className = className;
        this.params = params;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public <T, R> List<String> getList(DoraemonFunction<T, R> df) {
        Object o = getObject(df);
        return (List<String>) o;
    }

    public <T, R> String[] getArray(DoraemonFunction<T, R> df) {
        Object o = getObject(df);
        return ((List<String>) o).toArray(new String[0]);
    }

    public <T, R> String getString(DoraemonFunction<T, R> df) {
        Object o = getObject(df);
        return (String) o;
    }

    public <T, R> int getInt(DoraemonFunction<T, R> df) {
        Object o = getObject(df);
        return Integer.parseInt(o.toString());
    }

    public <T, R> Object getObject(DoraemonFunction<T, R> df) {
        String field = Methods.getFieldName(df);
        Object o = params.get(field);
        return o;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
