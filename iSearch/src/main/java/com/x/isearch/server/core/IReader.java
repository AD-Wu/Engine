package com.x.isearch.server.core;

/**
 * 数据读取抽象类
 *
 * @author AD
 * @date 2022/1/13 11:01
 */
public interface IReader<T> {

    /**
     * 读取数据
     *
     * @return
     * @throws Exception
     */
    void read(IDataListener<T> listener) throws Exception;
}
