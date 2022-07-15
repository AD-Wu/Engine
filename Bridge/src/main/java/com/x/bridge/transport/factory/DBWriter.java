package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.transport.core.IWriter;
import com.x.bridge.transport.mode.db.client.ClientWriteActor;
import com.x.bridge.transport.mode.db.client.IClientWriteActor;
import com.x.bridge.transport.mode.db.server.IServerWriteActor;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;
import com.x.doraemon.therad.BalanceExecutor;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import lombok.extern.log4j.Log4j2;

/**
 * 数据库传送
 * @author AD
 * @date 2022/6/25 13:05
 */
@Log4j2
public class DBWriter implements IWriter {


    private IProxy proxy;
    private IServerWriteActor serverWriter = new ServerWriteActor();
    private IClientWriteActor clientWriter = new ClientWriteActor();
    private ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
    private volatile boolean hasMsg = false;

    private ExecutorService writer = new BalanceExecutor<String>("DB-Writer", 1);

    public DBWriter(IProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void write(Message... msgs) throws Exception {
        if (msgs != null || msgs.length > 0) {
            log.info("即将写入数据【{}】条", msgs.length);
            if (proxy.isServerMode()) {
                serverWriter.saveBatch(Arrays.asList(msgs));
            } else {
                clientWriter.saveBatch(Arrays.asList(msgs));
            }
        }
    }

}
