package com.x.bridge.transport.interfaces;

/**
 * 传输对象
 * @author AD
 * @date 2022/6/25 12:49
 */
public interface ISender<T> {

    void send(T... ts);

}
