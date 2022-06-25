package com.x.bridge.proxy.factory;

import com.x.bridge.proxy.interfaces.IProxy;

/**
 * @author AD
 * @date 2022/6/25 17:52
 */
public class VMProxyServer implements IProxy {

    private final String proxyServer;

    public VMProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Override
    public String name() {
        return proxyServer;
    }

    @Override
    public boolean isServerMode() {
        return true;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void stop() {

    }

}
