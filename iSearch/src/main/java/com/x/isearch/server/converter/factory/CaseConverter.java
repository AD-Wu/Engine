package com.x.isearch.server.converter.factory;

import com.x.isearch.server.core.Context;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AD
 * @date 2022/3/17 0:47
 */
public class CaseConverter extends BaseConverter {


    @Override
    public void init(Context ctx) {
    }

    @Override
    public Map<String, Object> handle(Map<String, Object> row) {
        Map<String, Object> map = new LinkedHashMap<>();
        row.entrySet().stream().forEach(e -> {
            map.put(e.getKey().toLowerCase(), e.getValue());
        });
        return map;
    }

}
