package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;
import com.x.bridge.interfaces.IService;
import com.x.bridge.proxy.core.IReceiver;

/**
 * 传输引擎
 * @author AD
 * @date 2022/7/11 17:26
 */
public interface ITransportEngine extends IService, IWriter {

    IReceiver<Message> getReceiver();

}
