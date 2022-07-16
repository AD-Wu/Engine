package com.x.bridge.proxy.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/7/14 16:48
 */
public class ProxyConfig {

    // ------------------------ 静态变量------------------------
    protected static String host;
    // ------------------------ 变量定义 ------------------------
    private String name;
    private int connectTimeout;
    private String writeMode;
    private String readMode;

    private int port;
    private String appHost;
    private int appPort;
    private Set<String> allowClients;

    // ------------------------ 构造方法 ------------------------
    public ProxyConfig() {}
    // ------------------------ 方法定义 ------------------------


    public static String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
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

    static {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
