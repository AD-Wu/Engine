package com.x.plugin.core.listener;

/**
 * @author AD
 * @date 2022/5/17 11:06
 */
public interface IListener<T> {

    void onAccept(T t) throws Exception;
}
