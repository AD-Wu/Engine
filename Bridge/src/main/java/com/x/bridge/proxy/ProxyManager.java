package com.x.bridge.proxy;

import com.x.bridge.bean.Msg;
import com.x.bridge.dao.msg.IMsgDao;
import com.x.bridge.dao.msg.MsgDao;
import com.x.bridge.proxy.cmd.Cmd;
import com.x.bridge.proxy.cmd.ICmd;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.proxy.core.ProxyConfig;
import com.x.bridge.proxy.enums.MsgType;
import com.x.bridge.proxy.enums.ProxyStatus;
import com.x.bridge.proxy.socket.server.ProxyServer;
import com.x.doraemon.Arrayx;
import com.x.doraemon.Jsons;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import static java.util.stream.Collectors.toList;

/**
 * @author AD
 * @date 2022/7/16 12:58
 */
public final class ProxyManager {
    
    private static final IMsgDao serverReq = new MsgDao();
    
    private static final IMsgDao clientResp = new MsgDao();
    
    private static final IMsgDao clientReq = new MsgDao();
    
    private static final IMsgDao serverResp = new MsgDao();
    
    private static final Map<String, IProxyService> proxies = new HashMap<>();
    
    private static ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final WriteLock writeLock = lock.writeLock();
    private static final Condition creator = writeLock.newCondition();
    private static final ReadLock readLock = lock.readLock();
    
    public static boolean createProxy(ProxyConfig conf) {
        String name = conf.getName();
        if (!proxies.containsKey(name)) {
            try {
                writeLock.lock();
                if (!proxies.containsKey(name)) {
                    proxies.put(name, new ProxyServer(conf));
                    Msg req = new Msg();
                    req.setProxyName(name);
                    req.setCmd(Cmd.createProxyClient.code);
                    req.setType(MsgType.req.code);
                    req.setData(Jsons.toJson(conf).getBytes(StandardCharsets.UTF_8));
                    try {
                        sendMessage(name, req);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }  finally {
                writeLock.unlock();
            }
        }
        proxies.remove(name);
        return false;
    }
    
    public static boolean putProxy(IProxyService proxy) {
        String name = proxy.name();
        if (!proxies.containsKey(name)) {
            try {
                writeLock.lock();
                if (!proxies.containsKey(name)) {
                    proxy.status(ProxyStatus.created);
                    proxies.put(name, proxy);
                    return true;
                }
            } finally {
                writeLock.unlock();
            }
        }
        return false;
    }
    
    public static IProxyService removeProxy(String name) {
        if (proxies.containsKey(name)) {
            try {
                writeLock.lock();
                if (proxies.containsKey(name)) {
                    return proxies.remove(name);
                }
            } finally {
                writeLock.unlock();
            }
        }
        return null;
    }
    
    public static IProxyService getProxy(String name) {
        return proxies.get(name);
    }
    
    public static boolean startProxy(String name) {
        IProxyService proxy = proxies.get(name);
        if (proxy != null) {
            return proxy.start();
        }
        return false;
    }
    
    public static void stopProxy(String name) {
        IProxyService proxy = proxies.get(name);
        if (proxy != null) {
            proxy.stop();
        }
    }
    
    public static void sendMessage(String name, Msg... msgs) throws Exception {
        if (Arrayx.isNotEmpty(msgs)) {
            IProxyService proxy = getProxy(name);
            if (proxy != null) {
                if (proxy.isServerMode()) {
                    serverReq.saveBatch(Arrays.stream(msgs).collect(toList()));
                } else {
                    clientReq.saveBatch(Arrays.stream(msgs).collect(toList()));
                }
            } else {
                throw new RuntimeException("代理不存在");
            }
        }
    }
    
    public static void start() {
        timer.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                List<Msg> clients = clientResp.list();
                List<Msg> servers = serverResp.list();
                List<Long> clientIds = clients.stream().map(m -> m.getId()).collect(toList());
                List<Long> serversIds = clients.stream().map(m -> m.getId()).collect(toList());
                clientResp.removeBatchByIds(clientIds);
                serverResp.removeBatchByIds(serversIds);
                clients.forEach(m -> {
                    ICmd cmd = Cmd.get(m.getCmd());
                    IProxyService proxy = proxies.get(m.getProxyName());
                    cmd.execute(m, proxy);
                });
                servers.forEach(m -> {
                    ICmd cmd = Cmd.get(m.getCmd());
                    IProxyService proxy = proxies.get(m.getProxyName());
                    cmd.execute(m, proxy);
                });
            }
        }, 0, 0, TimeUnit.SECONDS);
    }
    
}
