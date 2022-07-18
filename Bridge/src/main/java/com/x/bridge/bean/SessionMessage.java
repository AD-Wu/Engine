package com.x.bridge.bean;

/**
 * 数据载体
 * @author AD
 * @date 2022/6/21 15:26
 */
public class SessionMessage extends Message {

    // ------------------------ 变量定义 ------------------------
    private static final long serialVersionUID = 1L;

    /**
     * 客户端地址
     */
    private String client;

    /**
     * 传输序号
     */
    private long seq;

    /**
     * 应用服务器Host
     */
    private String appHost;

    /**
     * 应用服务器端口
     */
    private int appPort;

    // ------------------------ 构造方法 ------------------------

    public SessionMessage() {}

    // ------------------------ 成员方法 ------------------------

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

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

}
