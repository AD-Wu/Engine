package com.x.mq.rabbit.data;

import com.x.mq.common.MQConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/23 12:48
 */
public class RabbitConfig extends MQConfig {

    /**
     * 自动重连
     */
    private boolean autoReconnect;
    /**
     * 虚拟机host
     */
    private String virtualHost;
    /**
     * 消费者线程数(回调监听时的线程数量)
     */
    private int threadCount;

    public RabbitConfig() {
        this("localhost", "5672", "guest", "guest");
    }

    public RabbitConfig(String host, String port, String user, String pwd) {
        super("rabbit");
        this.host = host;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
        this.autoReconnect = true;
        this.virtualHost = "/";
        this.threadCount = Runtime.getRuntime().availableProcessors() * 2;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
