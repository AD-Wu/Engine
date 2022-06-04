package com.x.isearch.server.core;

/**
 * 数据持久化抽象接口
 *
 * @author AD
 * @date 2022/1/13 11:02
 */
public interface IWriter<T> {

    /**
     * 写入数据
     *
     * @param t
     * @throws Exception
     */
    void write(T t) throws Exception;

}
