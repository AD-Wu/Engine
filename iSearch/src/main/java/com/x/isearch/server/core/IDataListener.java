package com.x.isearch.server.core;

/**
 * @author AD
 * @date 2022/1/13 15:39
 */
public interface IDataListener<T> {

    int add(T rows)throws Exception;

    int delete(T rows)throws Exception;

    int deleteAll()throws Exception;

    int update(T data)throws Exception;

}
