package com.x.plugin.facotry;

import com.x.plugin.anno.RunPlugin;
import com.x.plugin.core.IBeanGetter;
import com.x.plugin.util.StringHelper;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author AD
 * @date 2022/5/24 17:54
 */
public class DefaultBeanGetter implements IBeanGetter<Object> {

    @Override
    public String getPluginId(RunPlugin plugin, Object pluginParam) {

        String mgtOrgCodeKey = plugin.mgtOrgCode();
        String id = plugin.pluginId();
        if (StringHelper.isNull(id)) {
            Object o = getParam(pluginParam, mgtOrgCodeKey);
            if (o != null) {
                return o.toString();
            }
        }
        return id;
    }

    @Override
    public String getBeanName(RunPlugin plugin, Object pluginParam) {
        String sceneNoKey = plugin.sceneNo();
        Object o = getParam(pluginParam, sceneNoKey);
        if (o != null) {
            return o.toString();
        }
        return "";
    }

    private Object getParam(Object param, String key) {
        String[] keys = key.split("\\.");
        String k = null;
        int i = 0;
        // map 或 POJO类
        while (i < keys.length && (k = keys[i]) != null) {
            if (param instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) param;
                Object o = map.get(k);
                param = o;
            } else {
                try {
                    String methodName = "get" + StringHelper.firstToUpperCase(k);
                    Method method = param.getClass().getDeclaredMethod(methodName);
                    Object value = method.invoke(param);
                    param = value;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
        return param;
    }

}
