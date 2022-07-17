package com.x.bridge.web.api.proxy.data;

import com.x.bridge.enums.TransportMode;

/**
 * 代理客户端请求参数
 * @author AD
 * @date 2022/7/17 10:47
 */
public class ProxyClientReq {
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
    
}
