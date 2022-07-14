package com.x.bridge.proxy.interfaces;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/7/14 16:48
 */
public class ProxyConfig {
    // ------------------------ 变量定义 ------------------------
    protected static String host;

    static {
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    protected String name;
    protected int connectTimeout;
    protected String writeMode;
    protected String readMode;
    // ------------------------ 构造方法 ------------------------
    public ProxyConfig() {}
    // ------------------------ 方法定义 ------------------------


    public static String getHost() {
        return host;
    }

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
