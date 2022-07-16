package com.x.bridge.proxy;

import com.x.bridge.proxy.client.ProxyClient;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.proxy.core.ProxyConfig;
import com.x.bridge.proxy.server.ProxyServer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AD
 * @date 2022/7/16 12:58
 */
public final class ProxyManager {

    private static final Map<String, IProxy> proxies = new ConcurrentHashMap<>();

    public static IProxy createProxy(ProxyConfig conf, boolean server) {
        if (!proxies.containsKey(conf.getName())) {
            synchronized (ProxyManager.class) {
                if (!proxies.containsKey(conf.getName())) {
                    if (server) {
                        proxies.put(conf.getName(), new ProxyServer(conf));
                    } else {
                        proxies.put(conf.getName(), new ProxyClient(conf));
                    }
                }
            }
        }
        return proxies.get(conf.getName());
    }
}
