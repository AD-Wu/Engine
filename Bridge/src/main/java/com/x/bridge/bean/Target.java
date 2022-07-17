package com.x.bridge.bean;

import java.io.Serializable;

/**
 * 代理目标
 * @author AD
 * @date 2022/7/18 00:06
 */
public class Target implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ------------------------ 变量定义 ------------------------
    private long id;
    
    private String name;
    
    private String host;
    
    private int port;
    
    // ------------------------ 构造方法 ------------------------
    public Target() {}
    // ------------------------ 方法定义 ------------------------
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
}
