package com.x.plugin.core.config;

import com.x.plugin.data.Config;
import com.x.plugin.enums.ConfigType;
import com.x.plugin.util.ConfigFileHelper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AD
 * @date 2022/5/3 10:04
 */
public interface IConfigGetter {

    /**
     * 过去所有配置文件(k,v)=(profile,Config)
     * @return
     */
    Map<String, Config> getConfigs();


    default Map<String, Object> getConfigContext(Config conf) throws IOException {
        if (ConfigType.yml == conf.getType()) {
            return ConfigFileHelper.loadYml(conf.getName(), conf.getUrl());
        } else if (ConfigType.properties == conf.getType()) {
            return ConfigFileHelper.loadProperties(conf.getName(), conf.getUrl());
        }
        return new LinkedHashMap<>();
    }

}
