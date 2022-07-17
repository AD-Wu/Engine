package com.x.bridge.web.api.proxy.data;

import com.x.bridge.proxy.enums.TransportMode;

import java.util.Set;
import java.util.StringJoiner;

/**
 * 代理客户端请求参数
 * @author AD
 * @date 2022/7/17 10:47
 */
public class ProxyReq {
    
    /**
     * 名称
     */
    protected String name;
    
    /**
     * 超时(秒)
     */
    protected int connectTimeout = 60;
    
    /**
     * 写模式
     */
    protected String writeMode = TransportMode.DB.toString();
    
    /**
     * 读模式
     */
    protected String readMode = TransportMode.DB.toString();
    
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
    
    @Override
    public String toString() {
        return new StringJoiner(", ", ProxyReq.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("connectTimeout=" + connectTimeout)
                .add("writeMode='" + writeMode + "'")
                .add("readMode='" + readMode + "'")
                .add("port=" + port)
                .add("appHost='" + appHost + "'")
                .add("appPort=" + appPort)
                .add("allowClients=" + allowClients)
                .toString();
    }
    
}
