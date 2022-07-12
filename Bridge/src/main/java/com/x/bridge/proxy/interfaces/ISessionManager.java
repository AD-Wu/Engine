package com.x.bridge.proxy.interfaces;

import com.x.bridge.netty.interfaces.ISocket;
import com.x.bridge.proxy.data.AgentConfig;
import com.x.bridge.proxy.session.Session;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author AD
 * @date 2022/7/12 12:17
 */
public interface ISessionManager extends ISocket {

    AgentConfig getAgentConfig();

    boolean isAccept(String clientHost);

    void addSession(String appClient, Session session);

    Session closeSession(String appClient);

    Session getSession(String appClient);

    Session createSession(String appClient);

    Session createSession(ChannelHandlerContext ctx, String appClient);


}
