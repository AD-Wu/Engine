package com.x.bridge.proxy.core;

import com.x.bridge.bean.Message;
import com.x.bridge.session.Session;

/**
 * @author AD
 * @date 2022/7/14 10:26
 */
public interface ICommand {

    void execute(Message msg, Session session, IProxy proxy);

    int code();
}
