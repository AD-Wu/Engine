package com.x.bridge.proxy.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代理配置
 * @author AD
 * @date 2022/6/21 14:53
 */

public class AgentConfig {

    // ------------------------ 变量定义 ------------------------
    private static String agentHost;

    static {
        try {
            agentHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private int agentPort;
    private String appHost;
    private int appPort;
    private int connectTimeout;
    private String writeMode;
    private String readMode;
    private Set<String> allowClients;

    // ------------------------ 构造方法 ------------------------
    public AgentConfig() {}
    // ------------------------ 方法定义 ------------------------


    public static String getAgentHost() {
        return agentHost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgentPort() {
        return agentPort;
    }

    public void setAgentPort(int agentPort) {
        this.agentPort = agentPort;
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
