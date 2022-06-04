package com.x.plugin.core.filter;

/**
 * @author AD
 * @date 2022/4/25 20:48
 */
public interface IFilter<T> {

    boolean accept(T t);
}
