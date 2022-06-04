package com.x.isearch.server.data.config.source;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/24 20:02
 */
public class EsSourceConfig {
    /**
     * 数据源名,唯一标识
     */
    private String name;
    /**
     * 地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
        EsSourceConfig that = (EsSourceConfig) o;
        return port == that.port && Objects.equal(host, that.host) && Objects.equal(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, port, user);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
