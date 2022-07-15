package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.TransportEngineStatus;
import com.x.bridge.interfaces.IService;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface ITransportEngine extends IService, IWriter {

    IReceiver<Message> getReceiver();

    TransportEngineStatus status();

}
