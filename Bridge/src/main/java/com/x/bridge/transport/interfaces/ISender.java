package com.x.bridge.transport.interfaces;

import com.x.bridge.proxy.conf.ProxyConfig;

/**
 * 传输对象
 * @author AD
 * @date 2022/6/25 12:49
 */
public interface ISender<T> {

    void send(T... ts);

    void start(ProxyConfig conf);
}
