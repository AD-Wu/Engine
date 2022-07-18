package com.x.bridge.bus.core;

/**
 * @author AD
 * @date 2022/6/25 13:55
 */
public interface IReceiver<T> {

    void receive(T... ts);
}
