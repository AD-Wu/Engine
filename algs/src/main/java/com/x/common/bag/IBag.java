package com.x.common.bag;

/**
 * TODO
 *
 * @author chunquanw
 * @date 2021/11/25 20:57
 */
public interface IBag<T> extends Iterable {

    void add(T t);

    boolean isEmpty();
    long size();
}
