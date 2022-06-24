package com.x.bridge.proxy.core;

/**
 * 数据载体
 * @author AD
 * @date 2022/6/21 15:26
 */
public class Message {
    
    private String appClient;
    
    private String appHost;
    
    private int appPort;
    
    private String proxyServer;
    
    private final String cmd;
    
    private final long seq;
    
    private final byte[] data;
    
    Message(long seq, byte[] data, String cmd) {
        this.seq = seq;
        this.data = data;
        this.cmd = cmd;
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
    
    public String getProxyServer() {
        return proxyServer;
    }
    
    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }
   
    public String getCmd() {
        return cmd;
    }
    
    public long getSeq() {
        return seq;
    }
    
    public byte[] getData() {
        return data;
    }
    
}
