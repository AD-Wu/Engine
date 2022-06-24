package com.x.bridge.proxy;

import com.x.bridge.proxy.interfaces.IProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理管理者
 * @author AD
 * @date 2022/6/25 00:20
 */
public final class ProxyManager {
    
    private static final Map<String, Map<String, IProxy>> proxies = new HashMap<>();
    
    public static void closeProxyClient(String proxyServer, String appClient) {
        Map<String, IProxy> clients = proxies.get(proxyServer);
        if (clients != null) {
            synchronized (clients) {
                IProxy client = clients.remove(appClient);
                if (client != null) {
                    client.stop();
                }
            }
            
        }
    }
    
    public static void putProxyClient(String proxyServer, String appClient, IProxy client) {
        if (proxies.containsKey(proxyServer)) {
            Map<String, IProxy> clients = proxies.get(proxyServer);
            if (!clients.containsKey(appClient)) {
                synchronized (clients) {
                    if (!clients.containsKey(appClient)) {
                        clients.put(appClient, client);
                    }
                }
            }
        } else {
            synchronized (proxies) {
                if (!proxies.containsKey(proxyServer)) {
                    proxies.put(proxyServer, new HashMap<>());
                }
            }
        }
    }
    
}
