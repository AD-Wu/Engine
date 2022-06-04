package com.x.plugin.data;

import com.x.plugin.enums.ConfigType;
import java.net.URL;
import java.util.Map;

/**
 * @author AD
 * @date 2022/4/26 21:18
 */
public class Config{

    /**
     * 配置文件名
     */
    private String name;
    /**
     * 配置文件URL,如:jar:file:/D:/xxx.jar!/application.yml
     */
    private URL url;

    /**
     * 是否根配置文件,如:bootstrap.yml
     */
    private boolean isBoot;
    /**
     * 是否主要配置文件,如:application.yml
     */
    private boolean isMain;
    /**
     * 其它环境配置文件,如:dev,prod
     */
    private String profile;
    /**
     * 配置文件类型,如:yml,yaml,properties
     */
    private ConfigType type;

    /**
     * 配置内容
     */
    private Map<String, Object> context;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean isBoot() {
        return isBoot;
    }

    public void setBoot(boolean boot) {
        isBoot = boot;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
