package com.x.bridge.proxy.core;

/**
 * 代理上下文
 * @author AD
 * @date 2022/6/24 20:38
 */
public class ProxyContext {
    
    // ------------------------ 变量定义 ------------------------
    
    private String proxyServer;
    
    private String appClient;
    
    private String appHost;
    
    private int appPort;
    
    // ------------------------ 构造方法 ------------------------
    
    public ProxyContext() {}
    
    // ------------------------ 方法定义 ------------------------
    
    public String getProxyServer() {
        return proxyServer;
    }
    
    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }
  
    public String getAppClient() {
        return appClient;
    }
    
    public void setAppClient(String appClient) {
        this.appClient = appClient;
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
    
}
