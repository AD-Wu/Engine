package com.x.isearch.server.core;

/**
 * @author AD
 * @date 2022/2/18 21:26
 */
public interface IConverter<T, R> {

    String getClassName();

    void init(Context ctx);

    R handle(T t) throws Exception;

}
