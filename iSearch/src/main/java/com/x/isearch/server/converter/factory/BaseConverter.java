package com.x.isearch.server.converter.factory;

import com.x.isearch.server.core.IConverter;
import java.util.Map;

/**
 * @author AD
 * @date 2022/3/26 17:43
 */
public abstract class BaseConverter implements IConverter<Map<String, Object>, Map<String, Object>> {

    protected BaseConverter(){}

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

}
