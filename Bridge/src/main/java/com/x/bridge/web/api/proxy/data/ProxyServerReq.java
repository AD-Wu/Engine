package com.x.bridge.web.api.proxy.data;

import java.util.Set;

/**
 * 代理服务端请求参数
 * @author AD
 * @date 2022/7/17 10:40
 */
public class ProxyServerReq extends ProxyClientReq{
    
    
    /**
     * 代理端口
     */
    private int port;
    
    /**
     * 应用host
     */
    private String appHost;
    
    /**
     * 应用端口
     */
    private int appPort;
    
    /**
     * 白名单
     */
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
    
    
}
