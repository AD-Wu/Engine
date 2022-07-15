package com.x.bridge.transport.core;

/**
 * @author AD
 * @date 2022/6/25 13:55
 */
public interface IReceiver<T> {

    void receive(T... ts);
}
