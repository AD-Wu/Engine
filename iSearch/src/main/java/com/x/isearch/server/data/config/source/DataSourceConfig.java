package com.x.isearch.server.data.config.source;

import com.google.common.base.Objects;

/**
 * @author AD
 * @date 2022/3/24 17:41
 */
public class DataSourceConfig {
    /**
     * 数据源名,唯一标识
     */
    private String name;
    /**
     * 数据库URL
     */
    private String url;
    /**
     * 驱动类名
     */
    private String driver;
    /**
     * 用户
     */
    private String user;
    /**
     * 密码
     */
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSourceConfig that = (DataSourceConfig) o;
        return Objects.equal(url.split("\\?")[0], that.url.split("\\?")[0]) && Objects.equal(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url, user);
    }
}
