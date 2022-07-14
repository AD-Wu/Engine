package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;

/**
 * 传输对象
 * @author AD
 * @date 2022/6/25 12:49
 */
public interface IWriter {

    void write(Message... msgs) throws Exception;

}
