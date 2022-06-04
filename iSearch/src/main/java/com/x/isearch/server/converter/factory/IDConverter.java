package com.x.isearch.server.converter.factory;

import com.x.isearch.server.core.Context;
import java.util.Map;

/**
 * @author AD
 * @date 2022/3/17 1:49
 */
public class IDConverter extends BaseConverter {

    private String id;

    @Override
    public void init(Context ctx) {
    }

    @Override
    public Map<String, Object> handle(Map<String, Object> map) {
        map.put("id", map.remove(id));
        return map;
    }

}
