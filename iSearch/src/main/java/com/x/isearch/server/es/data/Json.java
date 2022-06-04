package com.x.isearch.server.es.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AD
 * @date 2022/2/26 22:23
 */
public class Json {

    private final Map<String, Object> properties;

    public Json() {
        this.properties = new LinkedHashMap<>();
    }

    public synchronized Json add(String key, Object value) {
        if (value instanceof Json) {
            Json json = (Json) value;
            put(properties, key, json.properties);
        } else {
            put(properties, key, value);
        }
        return this;
    }

    public Map<String, Object> get() {
        return Collections.unmodifiableMap(properties);
    }

    public synchronized Object get(String treeKey) {
        Map<String, Object> next = properties;
        int index = 0;
        String[] keys = treeKey.split("\\.");
        while (index < keys.length - 1) {
            String key = keys[index].trim();
            if (next.containsKey(key) && next.get(key) instanceof Map) {
                next = (Map<String, Object>) next.get(key);
                index++;
            } else {
                return null;
            }
        }
        return next.get(keys[index].trim());
    }

    private void put(Map<String, Object> currentMap, String treeKey, Object value) {
        int index = 0;
        String[] keys = treeKey.split("\\.");
        while (index < keys.length - 1) {
            String key = keys[index].trim();
            if (currentMap.containsKey(key) && currentMap.get(key) instanceof Map) {
                // 取得下一个map
                currentMap = (Map<String, Object>) currentMap.get(key);
                index++;
            } else {
                Map<String, Object> next = new LinkedHashMap<>();
                currentMap.put(keys[index], next);
                currentMap = next;
                index++;
            }
        }
        currentMap.put(keys[index].trim(), value);
    }

}
