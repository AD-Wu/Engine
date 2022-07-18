package com.x.bridge.bus.core;

import com.x.bridge.bean.SessionMessage;
import com.x.bridge.proxy.enums.BusStatus;
import com.x.bridge.netty.common.IService;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface IBus extends IService, IWriter<SessionMessage> {

    IReceiver<SessionMessage> receiver();

    BusStatus status();

    void clear();

}
