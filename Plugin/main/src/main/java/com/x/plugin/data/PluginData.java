package com.x.plugin.data;

/**
 * 插件数据
 * @author AD
 * @date 2022/4/26 21:09
 */
public class PluginData {

    /**
     * 插件环境id
     */
    private String envId;
    /**
     * 插件环境名
     */
    private String envName;
    /**
     * 插件名(文件名)
     */
    private String name;

    /**
     * jar包大小(单位: B,KB,MB,GB)
     */
    private String size;

    /**
     * bean数组
     */
    private BeanData[] beans;

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public BeanData[] getBeans() {
        return beans;
    }

    public void setBeans(BeanData[] beans) {
        this.beans = beans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
