package com.x.bridge.bus.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.bridge.bean.SessionMessage;
import com.x.bridge.dao.session.ISessionMessageDao;
import com.x.bridge.dao.session.SessionMessageDao;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.bus.core.IWriter;
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
public class DBWriter implements IWriter<SessionMessage> {

    private final IProxyService proxy;

    private final ISessionMessageDao serverWriter = new SessionMessageDao();

    private final ISessionMessageDao clientWriter = new SessionMessageDao();

    private final Queue<SessionMessage> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);

    private  volatile boolean hasMsg = false;

    private final ExecutorService writer = new BalanceExecutor<String>("DB-Writer", 1);

    public DBWriter(IProxyService proxy) {
        this.proxy = proxy;
    }

    @Override
    public void write(SessionMessage... msgs) throws Exception {
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
        LambdaQueryWrapper<SessionMessage> query = new LambdaQueryWrapper<>();
        query.eq(SessionMessage::getProxyName, proxy.name());
        if (proxy.isServerMode()) {
            serverWriter.remove(query);
        } else {
            clientWriter.remove(query);
        }
    }

}
