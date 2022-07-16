package com.x.bridge.bean;

import com.x.bridge.enums.MessageType;
import com.x.bridge.session.Session;

import java.io.Serializable;

/**
 * 数据载体
 * @author AD
 * @date 2022/6/21 15:26
 */
public class Message implements Serializable {
    
    private static final long serialVersionUID = -1L;
    
    // ------------------------ 变量定义 ------------------------
    
    /**
     * 主键
     */
    private long id;
    
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
    private String proxyName;
    
    /**
     * 应用客户端地址
     */
    private String appClient;
    
    /**
     * 消息类型
     */
    private int type;
    
    /**
     * 命令
     */
    private int cmd;
    
    /**
     * 传输序号
     */
    private long seq;
    
    /**
     * 应用数据
     */
    private byte[] data;
    
    // ------------------------ 构造方法 ------------------------
    
    public Message() {}
    
    // ------------------------ 静态方法 ------------------------
    public static Message newCommand(String proxyName, int cmd, String client) {
        return newCommand(proxyName, cmd, client, null);
    }
    
    public static Message newCommand(String proxyName, int cmd, String client, byte[] data) {
        Message msg = new Message();
        msg.setProxyName(proxyName);
        msg.setAppClient(client);
        msg.setCmd(cmd);
        msg.setType(MessageType.command.code);
        msg.setSeq(Session.seq);
        msg.setData(data);
        return msg;
    }
    
    // ------------------------ 成员方法 ------------------------
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
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
    
    public String getProxyName() {
        return proxyName;
    }
    
    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }
    
    public String getAppClient() {
        return appClient;
    }
    
    public void setAppClient(String appClient) {
        this.appClient = appClient;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getCmd() {
        return cmd;
    }
    
    public void setCmd(int cmd) {
        this.cmd = cmd;
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
