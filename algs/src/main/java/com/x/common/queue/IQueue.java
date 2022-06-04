package com.x.common.queue;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/25 19:33
 */
public interface IQueue<T> {

    void enqueue(T t);

    T dequeue();

    boolean isEmpty();
}
