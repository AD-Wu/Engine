package com.x.doraemon.common.interfaces;

/**
 * @author AD
 * @date 2022/4/10 18:55
 */
public interface IFilter<T> {

    boolean accept(T t);
}
