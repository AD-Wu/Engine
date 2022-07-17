package com.x.bridge.transport.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.dao.session.ISessionMsgDao;
import com.x.bridge.dao.session.SessionMsgDao;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.transport.core.IWriter;
import com.x.doraemon.therad.BalanceExecutor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * 数据库传送
 * @author AD
 * @date 2022/6/25 13:05
 */
@Log4j2
public class DBWriter implements IWriter<SessionMsg> {
    
    private final IProxyService proxy;
    
    private final ISessionMsgDao serverWriter = new SessionMsgDao();
    
    private final ISessionMsgDao clientWriter = new SessionMsgDao();
    
    private final Queue<SessionMsg> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
    
    private  volatile boolean hasMsg = false;
    
    private final ExecutorService writer = new BalanceExecutor<String>("DB-Writer", 1);
    
    public DBWriter(IProxyService proxy) {
        this.proxy = proxy;
    }
    
    @Override
    public void write(SessionMsg... msgs) throws Exception {
        if (msgs != null || msgs.length > 0) {
            log.info("即将写入数据【{}】条", msgs.length);
            if (proxy.isServerMode()) {
                serverWriter.saveBatch(Arrays.asList(msgs));
            } else {
                clientWriter.saveBatch(Arrays.asList(msgs));
            }
        }
    }
    
    @Override
    public void clear() {
        LambdaQueryWrapper<SessionMsg> query = new LambdaQueryWrapper<>();
        query.eq(SessionMsg::getProxyName, proxy.name());
        if (proxy.isServerMode()) {
            serverWriter.remove(query);
        } else {
            clientWriter.remove(query);
        }
    }
    
}
