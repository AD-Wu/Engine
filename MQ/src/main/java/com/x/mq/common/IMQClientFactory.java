package com.x.mq.common;

import java.io.Closeable;

/**
 * @author AD
 * @date 2022/3/24 12:21
 */
public interface IMQClientFactory<T, E> extends Closeable {

    IMQClient<T,E> createClient(E e);
}
