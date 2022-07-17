package com.x.bridge.proxy.cmd;

import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.proxy.core.Session;

/**
 * @author AD
 * @date 2022/7/14 10:26
 */
public interface ISessionCmd {

    void execute(SessionMsg msg, Session session, IProxyService proxy);

    int code();
}
