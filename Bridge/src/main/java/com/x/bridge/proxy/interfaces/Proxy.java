package com.x.bridge.proxy.interfaces;

import com.x.bridge.session.ISessionManager;
import com.x.bridge.session.SessionManager;
import com.x.bridge.transport.interfaces.ITransportEngine;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 17:02
 */
@Log4j2
public abstract class Proxy implements IProxy{

    protected String name;
    protected ProxyConfig conf;
    protected ITransportEngine transporter;
    protected ISessionManager sessions;

    public Proxy(String name, ProxyConfig conf) {
        this.name = name;
        this.conf = conf;
        this.sessions = new SessionManager(name);
        this.transporter = null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isServerMode() {
        return false;
    }

    @Override
    public ISessionManager getSessionManager() {
        return sessions;
    }

    @Override
    public ITransportEngine getTransportEngine() {
        return transporter;
    }

    @Override
    public ProxyConfig getConfig() {
        return conf;
    }

}
