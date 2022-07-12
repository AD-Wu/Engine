package com.x.bridge.proxy.interfaces;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.enums.TransportEngineStatus;
import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.transport.interfaces.IReceiver;

/**
 * 代理服务器接口
 * @author AD
 * @date 2022/6/21 14:52
 */
public interface IProxy extends ISocket, IReceiver<Message> {

    ProxyStatus status();

    TransportEngineStatus transportStatus();

    default boolean isAccept(String host) {
        return true;
    }

}
