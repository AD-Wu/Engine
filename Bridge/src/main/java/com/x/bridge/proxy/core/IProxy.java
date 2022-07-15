package com.x.bridge.proxy.core;

import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.interfaces.IService;
import com.x.bridge.session.ISessionManager;
import com.x.bridge.transport.core.ITransporter;

/**
 * 代理服务器接口
 * @author AD
 * @date 2022/6/21 14:52
 */
public interface IProxy extends IService {

    String name();

    boolean isServerMode();

    default boolean isAccept(String client) {
        return true;
    }

    ISessionManager getSessionManager();

    ITransporter getTransporter();

    ProxyStatus status();

    ProxyConfig getConfig();

}
