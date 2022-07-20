package com.x.bridge.bean;

import java.io.Serializable;

/**
 * 代理对象
 * @author AD
 * @date 2022/7/18 00:02
 */
public class Proxy implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ------------------------ 变量定义 ------------------------
    private long id;
    
    private long tid;
    
    private String name;
    
    private boolean serverMode;
    
    private boolean out;
    
    private int timeout;
    
    private String writeMode;
    
    private String readMode;
    
    private String host;
    
    private int port;
    
    // ------------------------ 构造方法 ------------------------
    public Proxy() {}
    // ------------------------ 方法定义 ------------------------
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getTid() {
        return tid;
    }
    
    public void setTid(long tid) {
        this.tid = tid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isServerMode() {
        return serverMode;
    }
    
    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }
    
    public boolean isOut() {
        return out;
    }
    
    public void setOut(boolean out) {
        this.out = out;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
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
