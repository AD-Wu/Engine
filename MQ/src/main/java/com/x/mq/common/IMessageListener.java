package com.x.mq.common;

/**
 * @author AD
 * @date 2022/3/23 13:55
 */
public interface IMessageListener<T> {

    boolean onReceive(T t);
}
