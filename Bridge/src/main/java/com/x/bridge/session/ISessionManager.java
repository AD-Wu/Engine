package com.x.bridge.session;

import com.x.bridge.bean.Message;
import com.x.bridge.interfaces.IService;
import com.x.bridge.transport.core.IReceiver;

/**
 * @author AD
 * @date 2022/7/14 16:30
 */
public interface ISessionManager extends IService, IReceiver<Message> {

    String name();

    boolean existSession(String client);

    void putSession(String client, Session session);

    Session removeSession(String client);

    Session getSession(String client) ;

    void sync();

}
