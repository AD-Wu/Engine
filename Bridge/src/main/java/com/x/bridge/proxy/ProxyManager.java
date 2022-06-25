package com.x.bridge.proxy;

import com.x.bridge.proxy.core.Replier;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理管理者
 * @author AD
 * @date 2022/6/25 00:20
 */
public final class ProxyManager {

    private static final Map<String, Map<String, Replier>> proxies = new HashMap<>();

    public static void closeReplier(String proxyServer, String appClient) {
        Map<String, Replier> clients = proxies.get(proxyServer);
        if (clients != null) {
            synchronized (clients) {
                Replier replier = clients.remove(appClient);
                if (replier != null) {
                    replier.close();
                }
            }
        }
    }

    public static void putReplier(String proxyServer, String appClient, Replier replier) {
        if (proxies.containsKey(proxyServer)) {
            Map<String, Replier> clients = proxies.get(proxyServer);
            if (!clients.containsKey(appClient)) {
                synchronized (clients) {
                    if (!clients.containsKey(appClient)) {
                        clients.put(appClient, replier);
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
