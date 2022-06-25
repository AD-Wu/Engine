package com.x.bridge.proxy.conf;

import java.util.Set;
import lombok.Data;

/**
 * 代理配置
 * @author AD
 * @date 2022/6/21 14:53
 */
@Data
public class ProxyConfig {

    // ------------------------ 变量定义 ------------------------

    private String name;
    private int proxyPort;
    private String appHost;
    private int appPort;
    private int connectTimeout;
    private String sendMode;
    private Set<String> allowClients;

    // ------------------------ 构造方法 ------------------------
    public ProxyConfig() {}
    // ------------------------ 方法定义 ------------------------

}
