package com.x.bridge.session;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.IProxy;

/**
 * @author AD
 * @date 2022/7/16 15:49
 */
public interface ISession {

    String key();

    boolean isConnected();

    void setConnected(boolean connected);

    void close();

    void sendToProxy();

    void sentToApp();

    void receive(Message... msgs);

    IProxy getProxy();
}
