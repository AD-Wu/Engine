package com.x.bridge.proxy.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.proxy.interfaces.IProxy;

/**
 * @author AD
 * @date 2022/7/11 19:49
 */
public class Proxy implements IProxy {

    @Override
    public ProxyStatus status() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isServerMode() {
        return false;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void receive(Message... messages) {

    }

    @Override
    public void send(Message... messages) {

    }
}
