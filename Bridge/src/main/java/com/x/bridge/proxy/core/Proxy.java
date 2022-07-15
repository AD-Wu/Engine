package com.x.bridge.proxy.core;

import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.enums.TransportMode;
import com.x.bridge.session.ISessionManager;
import com.x.bridge.session.SessionManager;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.core.ITransporter;
import com.x.bridge.transport.core.IWriter;
import com.x.bridge.transport.core.Transporter;
import lombok.extern.log4j.Log4j2;

/**
 * @author AD
 * @date 2022/7/14 17:02
 */
@Log4j2
public abstract class Proxy implements IProxy {

    protected String name;
    protected ProxyConfig conf;
    protected volatile ProxyStatus status;
    protected ITransporter transporter;
    protected ISessionManager sessions;

    public Proxy(String name, ProxyConfig conf) {
        this.name = name;
        this.conf = conf;
        this.status = ProxyStatus.stopped;
        this.sessions = new SessionManager(this);
        IReader reader = TransportMode.get(conf.getReadMode()).createReader(this);
        IWriter writer = TransportMode.get(conf.getWriteMode()).createWriter(this);
        this.transporter = new Transporter(this, reader, writer);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ProxyStatus status() {
        return status;
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
    public ITransporter getTransporter() {
        return transporter;
    }

    @Override
    public ProxyConfig getConfig() {
        return conf;
    }

}
