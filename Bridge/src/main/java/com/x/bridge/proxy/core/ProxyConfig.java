package com.x.bridge.proxy.core;

import com.x.bridge.proxy.enums.BusType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Set;

/**
 * @author AD
 * @date 2022/7/14 16:48
 */
public class ProxyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------ 变量定义 ------------------------
    private String name;

    private int timeout = 60;

    private String writeMode = BusType.DB.toString();

    private String readMode = BusType.DB.toString();

    private int port;

    private String appHost;

    private int appPort;

    private Set<String> allowClients;

    // ------------------------ 构造方法 ------------------------
    public ProxyConfig() {}
    // ------------------------ 方法定义 ------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getWriteMode() {
        return writeMode;
    }

    public void setWriteMode(String writeMode) {
        this.writeMode = writeMode;
    }

    public String getReadMode() {
        return readMode;
    }

    public void setReadMode(String readMode) {
        this.readMode = readMode;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAppHost() {
        return appHost;
    }

    public void setAppHost(String appHost) {
        this.appHost = appHost;
    }

    public int getAppPort() {
        return appPort;
    }

    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }

    public Set<String> getAllowClients() {
        return allowClients;
    }

    public void setAllowClients(Set<String> allowClients) {
        this.allowClients = allowClients;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
