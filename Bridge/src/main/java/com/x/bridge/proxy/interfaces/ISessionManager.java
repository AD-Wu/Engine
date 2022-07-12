package com.x.bridge.proxy.interfaces;

import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.proxy.data.AgentConfig;
import com.x.bridge.proxy.session.Session;

/**
 * @author AD
 * @date 2022/7/12 12:17
 */
public interface ISessionManager extends ISocket {

    AgentConfig getAgentConfig();

    public boolean isAccept(String clientHost);

    public void addSession(String appClient, Session session);

    public Session closeSession(String appClient);

    public Session getSession(String appClient);

}
