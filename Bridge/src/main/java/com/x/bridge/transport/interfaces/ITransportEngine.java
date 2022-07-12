package com.x.bridge.transport.interfaces;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.TransportEngineStatus;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface ITransportEngine extends IWriter<Message> {

    boolean start();

    void stop();

    TransportEngineStatus status();

}
