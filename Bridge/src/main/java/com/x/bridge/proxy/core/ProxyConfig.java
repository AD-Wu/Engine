package com.x.bridge.proxy.core;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * 代理配置
 * @author AD
 * @date 2022/6/21 14:53
 */
@Getter
@Setter
public class ProxyConfig {
    
    private String name;
    private int proxyPort;
    private Set<String> allowClients;
}
