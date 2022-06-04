package com.x.plugin.core.filter;

import com.x.plugin.enums.PluginProperties.Key;
import com.x.plugin.util.StringHelper;
import java.util.Properties;

/**
 * @author AD
 * @date 2022/5/17 11:14
 */
public class PropFilter implements IFilter<Properties> {

    @Override
    public boolean accept(Properties prop) {
        for (Key key : Key.values()) {
            String value = prop.getProperty(key.toString());
            if(StringHelper.isNull(value)){
                return false;
            }
        }
        return true;
    }
}
