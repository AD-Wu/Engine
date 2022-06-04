package com.x.common.stack;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/24 20:36
 */
public interface IStack<T> {

    void push(T t);

    T pop();

    boolean isEmpty();

    long size();
}
