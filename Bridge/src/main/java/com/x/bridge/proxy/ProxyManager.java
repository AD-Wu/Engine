package com.x.bridge.proxy;

import com.x.bridge.proxy.session.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理管理者
 * @author AD
 * @date 2022/6/25 00:20
 */
public final class ProxyManager {

    private static final Map<String, Map<String, Session>> proxies = new HashMap<>();

    public static void closeReplier(String proxyServer, String appClient) {
        Map<String, Session> clients = proxies.get(proxyServer);
        if (clients != null) {
            synchronized (clients) {
                Session session = clients.remove(appClient);
                if (session != null) {
                    session.close();
                }
            }
        }
    }

    public static void putReplier(String proxyServer, String appClient, Session session) {
        if (proxies.containsKey(proxyServer)) {
            Map<String, Session> clients = proxies.get(proxyServer);
            if (!clients.containsKey(appClient)) {
                synchronized (clients) {
                    if (!clients.containsKey(appClient)) {
                        clients.put(appClient, session);
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
