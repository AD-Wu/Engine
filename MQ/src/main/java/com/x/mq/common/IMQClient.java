package com.x.mq.common;

/**
 * @author AD
 * @date 2022/3/24 12:21
 */
public interface IMQClient<MESSAGE, PARAM> {

    boolean existQueue(PARAM param) throws Exception;

    boolean createQueue(PARAM param) throws Exception;

    boolean deleteQueue(PARAM param) throws Exception;

    boolean send(MESSAGE msg) throws Exception;

    void onReceive(IMessageListener<MESSAGE> listener) throws Exception;
}
