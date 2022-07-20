package com.x.bridge.bean;

import java.io.Serializable;

/**
 * 代理服务端请求
 * @author AD
 * @date 2022/7/17 13:31
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private long id;

    /**
     * 代理服务器地址
     */
    private String proxyName;

    /**
     * 命令
     */
    private int cmd;

    /**
     * 消息类型
     */
    private int type;

    /**
     * 数据
     */
    private byte[] data;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
