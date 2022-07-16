package com.x.bridge.proxy.core;

import com.x.bridge.bean.Message;
import com.x.bridge.enums.Command;
import com.x.bridge.enums.MessageType;
import com.x.bridge.enums.ProxyStatus;
import com.x.bridge.enums.TransportMode;
import com.x.bridge.interfaces.Service;
import com.x.bridge.session.ISessionManager;
import com.x.bridge.session.Session;
import com.x.bridge.session.SessionManager;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.core.ITransporter;
import com.x.bridge.transport.core.IWriter;
import com.x.bridge.transport.core.Transporter;
import com.x.doraemon.Strings;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author AD
 * @date 2022/7/14 17:02
 */
@Log4j2
public abstract class Proxy extends Service implements IProxy {
    
    protected ProxyConfig conf;
    
    protected List<ProxyStatus> statusQueue;
    
    private volatile ProxyStatus status;
    
    private volatile ProxyStatus lastStatus;
    
    protected ITransporter transporter;
    
    protected ISessionManager sessions;
    
    public Proxy(ProxyConfig conf) {
        this.conf = conf;
        this.status = ProxyStatus.stopped;
        this.statusQueue = new LinkedList<>();
        this.sessions = new SessionManager(this);
        IReader reader = TransportMode.get(conf.getReadMode()).createReader(this);
        IWriter writer = TransportMode.get(conf.getWriteMode()).createWriter(this);
        this.transporter = new Transporter(this, reader, writer);
    }
    
    @Override
    public String name() {
        return conf.getName();
    }
    
    @Override
    public ProxyStatus status() {
        return status;
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
    
    @Override
    protected void onStartError(Throwable e) {
        stop();
    }
    
    @Override
    public void sync(boolean syncStart) {
        if (syncStart) {
            status(ProxyStatus.syncStart);
        } else {
            status(ProxyStatus.syncSession);
        }
        
        String clients = Strings.joining(",",getSessionManager().getSessionKeys());
        byte[] clientsBytes = null;
        if (clients != null && clients.length() > 0) {
            clientsBytes = clients.getBytes(StandardCharsets.UTF_8);
        }
        Message msg = new Message();
        msg.setAppClient("sync");
        msg.setProxyName(name());
        msg.setCmd(Command.sync.code);
        msg.setType(MessageType.command.code);
        msg.setSeq(Session.seq);
        msg.setData(clientsBytes);
        try {
            getTransporter().write(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void status(ProxyStatus status) {
        this.lastStatus = this.status;
        this.status = status;
    }
    
    protected void startTransporter() throws Exception {
        if (transporter.start()) {
            log.info("传输引擎启动成功");
        } else {
            status = ProxyStatus.error;
            log.error("传输引擎启动失败");
            throw new RuntimeException("传输引擎启动失败");
        }
    }
    
    protected void startSessionManager() throws Exception {
        if (sessions.start()) {
            log.info("会话管理启动成功");
        } else {
            status = ProxyStatus.error;
            log.info("会话管理启动失败");
            throw new RuntimeException("会话管理启动失败");
        }
    }
    
}
