package com.x.bridge.proxy.interfaces;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.netty.interfaces.INetty;
import com.x.bridge.transport.interfaces.IReceiver;
import com.x.bridge.transport.interfaces.ISender;

/**
 * 代理服务器接口
 * @author AD
 * @date 2022/6/21 14:52
 */
public interface IProxy extends INetty, ISender<Message>, IReceiver<Message> {

    default boolean isAccept(String host){
        return true;
    }

    ProxyStatus status();
}
