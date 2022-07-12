package com.x.bridge.netty.core;

import com.x.doraemon.Strings;
import lombok.extern.log4j.Log4j2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * socket配置
 * @author AD
 * @date 2022/6/21 12:13
 */
@Log4j2
public class SocketConfig {

    /**
     * ip地址或域名
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * netty boss线程数
     */
    private int bossCount = 1;

    /**
     * netty worker线程数，默认cup*4
     */
    private int workerCount = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * 多长时间没有读取数据则断开连接，默认0:不开启，单位：分钟
     */
    private int readTimeout = 0;

    /**
     * 多长时间没有写数据则断开连接，默认0:不开启，单位：分钟
     */
    private int writeTimeout = 0;

    /**
     * 空闲时断开连接，默认0:不开启，单位：分钟
     */
    private int idleTimeout = 0;

    /**
     * 资源占满时允许保留的3次握手客户端数量，0表示默认50个
     */
    private int backlog = 0;

    /**
     * tcp接收缓冲区大小
     */
    private int recvBuf = 65536;

    public static SocketConfig getServerConfig(int port) {
        try {
            return new SocketConfig(InetAddress.getLocalHost().getHostAddress(), port);
        } catch (UnknownHostException e) {
            log.error(Strings.getExceptionTrace(e));
        }
        return null;
    }

    public static SocketConfig getClientConfig(String host, int port) {
        try {
            return new SocketConfig(InetAddress.getByName(host).getHostAddress(), port);
        } catch (UnknownHostException e) {
            log.error(Strings.getExceptionTrace(e));
        }
        return null;
    }

    private SocketConfig(String host, int port) {
        this.host = host;
        this.port = Math.max(port, 0);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getBossCount() {
        return bossCount;
    }

    public void setBossCount(int bossCount) {
        this.bossCount = bossCount;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getRecvBuf() {
        return recvBuf;
    }

    public void setRecvBuf(int recvBuf) {
        this.recvBuf = recvBuf;
    }

}
