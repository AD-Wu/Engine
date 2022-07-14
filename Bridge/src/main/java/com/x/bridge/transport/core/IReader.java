package com.x.bridge.transport.core;

import com.x.bridge.bean.Message;

/**
 * @author AD
 * @date 2022/7/12 11:24
 */
public interface IReader {

    Message[] read() throws Exception;
}
