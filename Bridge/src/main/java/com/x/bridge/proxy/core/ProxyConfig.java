package com.x.bridge.proxy.core;

import java.util.Set;

/**
 * 代理配置
 * @author AD
 * @date 2022/6/21 14:53
 */
public class ProxyConfig {
    
    // ------------------------ 变量定义 ------------------------
    
    /**
     * 代理名称
     */
    private String name;
    
    /**
     * 代理端口
     */
    private int proxyPort;
    
    /**
     * 应用服务Host
     */
    private String appHost;
    
    /**
     * 应用服务端口
     */
    private int appPort;
    
    /**
     * socket连接超时时间
     */
    private int connectTimeout;
    
    /**
     * 白名单
     */
    private Set<String> allowClients;
    
    // ------------------------ 构造方法 ------------------------
    public ProxyConfig() {}
    
    // ------------------------ 方法定义 ------------------------
    
    /**
     * 获取代理名称
     * @return String 代理名称
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * 设置代理名称
     * @param name 代理名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取代理端口
     * @return int 代理端口
     */
    public int getProxyPort() {
        return this.proxyPort;
    }
    
    /**
     * 设置代理端口
     * @param proxyPort 代理端口
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    /**
     * 获取应用服务Host
     * @return String 应用服务Host
     */
    public String getAppHost() {
        return this.appHost;
    }
    
    /**
     * 设置应用服务Host
     * @param appHost 应用服务Host
     */
    public void setAppHost(String appHost) {
        this.appHost = appHost;
    }
    
    /**
     * 获取应用服务端口
     * @return int 应用服务端口
     */
    public int getAppPort() {
        return this.appPort;
    }
    
    /**
     * 设置应用服务端口
     * @param appPort 应用服务端口
     */
    public void setAppPort(int appPort) {
        this.appPort = appPort;
    }
    
    /**
     * 获取socket连接超时时间
     * @return int socket连接超时时间
     */
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    /**
     * 设置socket连接超时时间
     * @param connectTimeout socket连接超时时间
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    /**
     * 获取白名单
     * @return Set<String> 白名单
     */
    public Set<String> getAllowClients() {
        return this.allowClients;
    }
    
    /**
     * 设置白名单
     * @param allowClients 白名单
     */
    public void setAllowClients(Set<String> allowClients) {
        this.allowClients = allowClients;
    }
    
}
