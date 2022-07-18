package com.x.bridge.bus.core;

/**
 * @author AD
 * @date 2022/7/12 11:24
 */
public interface IReader<T> {

    T[] read() throws Exception;
}
