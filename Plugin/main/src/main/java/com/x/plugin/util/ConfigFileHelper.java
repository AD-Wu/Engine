package com.x.plugin.util;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.UrlResource;

/**
 * @author AD
 * @date 2022/5/6 22:42
 */
public class ConfigFileHelper {

    public static Map<String, Object> loadYml(String filename, URL url) throws IOException {
        return load(filename, url, new YamlPropertySourceLoader());
    }

    public static Map<String, Object> loadProperties(String filename, URL url) throws IOException {
        return load(filename, url, new PropertiesPropertySourceLoader());
    }

    public static Map<String, Object> load(String filename, URL url, PropertySourceLoader loader) throws IOException {
        if (url != null) {
            UrlResource resource = new UrlResource(url);
            if (loader != null) {
                List<PropertySource<?>> pss = loader.load(filename, resource);
                if (pss != null && pss.size() > 0) {
                    PropertySource<?> ps = pss.get(0);
                    if (ps != null) {
                        // 加载配置到环境中
                        Map<String, Object> prop = parseConfig(ps);
                        return prop;
                    }
                }
            }
        }
        return new LinkedHashMap<>();
    }

    public static Map<String, Object> parseConfig(PropertySource<?> ps) {
        Map<String, Object> prop = new LinkedHashMap<>();
        Object src = ps.getSource();
        if (src instanceof Map) {
            Map<String, Object> source = (Map<String, Object>) src;
            Iterator<Entry<String, Object>> it = source.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> next = it.next();
                String key = next.getKey();
                Object value = next.getValue();
                if (value instanceof OriginTrackedValue) {
                    value = ((OriginTrackedValue) value).getValue();
                }
                prop.put(key, value);
            }
        }
        return prop;
    }
}
