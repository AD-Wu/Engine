package com.x.bridge.transport.send;

/**
 * bean
 * @author AD
 * @date 2022/6/22 21:57
 */
public class AppData {
    
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
    private String appServerHost;
    
    /**
     * 应用服务器端口
     */
    private int appServerPort;
    
    /**
     * 代理服务器地址
     */
    private String proxyServer;
    
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
    
}
