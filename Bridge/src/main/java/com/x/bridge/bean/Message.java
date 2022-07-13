package com.x.bridge.bean;

import java.io.Serializable;

/**
 * 数据载体
 * @author AD
 * @date 2022/6/21 15:26
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private long id;

    /**
     * 应用客户端地址
     */
    private String appClient;

    /**
     * 应用服务器Host
     */
    private String appHost;

    /**
     * 应用服务器端口
     */
    private int appPort;

    /**
     * 代理服务器地址
     */
    private String agentServer;

    /**
     * 命令
     */
    private int cmdCode;

    /**
     * 传输序号
     */
    private long seq;

    /**
     * 应用数据
     */
    private byte[] data;

    public Message() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAgentServer() {
        return agentServer;
    }

    public void setAgentServer(String agentServer) {
        this.agentServer = agentServer;
    }

    public int getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(int cmd) {
        this.cmdCode = cmd;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
