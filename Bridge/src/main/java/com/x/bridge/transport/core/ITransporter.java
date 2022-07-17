package com.x.bridge.transport.core;

import com.x.bridge.bean.SessionMsg;
import com.x.bridge.proxy.enums.TransporterStatus;
import com.x.bridge.proxy.core.IService;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface ITransporter extends IService, IWriter<SessionMsg> {

    IReceiver<SessionMsg> getReceiver();

    TransporterStatus status();

    void clear();

}
