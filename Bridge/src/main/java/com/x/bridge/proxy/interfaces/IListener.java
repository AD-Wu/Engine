package com.x.bridge.proxy.interfaces;

/**
 * @author AD
 * @date 2022/6/25 12:42
 */
public interface IListener<T> {

    void success(T t);

    void fail(Throwable t);

    void timeout();
}
