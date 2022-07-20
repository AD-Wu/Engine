package com.x.bridge.bus.core;

import com.x.bridge.netty.common.IService;
import com.x.bridge.proxy.enums.BusStatus;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface IBus<T> extends IService, IWriter<T> {

    IReceiver<T> receiver();

    BusStatus status();

    void clear();

}
