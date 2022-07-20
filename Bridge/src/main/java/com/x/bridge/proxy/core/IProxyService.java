package com.x.bridge.proxy.core;

import com.x.bridge.bus.core.IBus;
import com.x.bridge.bus.core.IReceiver;
import com.x.bridge.netty.common.IService;
import com.x.bridge.proxy.core.socket.Session;
import com.x.bridge.proxy.enums.ProxyStatus;

/**
 * 代理服务器接口
 * @author AD
 * @date 2022/6/21 14:52
 */
public interface IProxyService<T> extends IService, IReceiver<T> {

    String name();

    boolean isServerMode();

    ProxyStatus status();

    void status(ProxyStatus status);

    ProxyConfig config();

    IBus getBus();

    void sync(boolean isStart);

    void syncSuccess();

    boolean existSession(String client);

    void putSession(String client, Session session);

    Session removeSession(String client);

    Session getSession(String client);

    String[] clients();

    default boolean isAccept(String client) {
        return true;
    }

}
