package com.x.bridge.proxy.server;

import com.x.bridge.proxy.core.ProxyConfig;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/7/14 16:31
 */
public class ProxyServerConfig extends ProxyConfig {

    private int port;
    private String appHost;
    private int appPort;
    private Set<String> allowClients;

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
