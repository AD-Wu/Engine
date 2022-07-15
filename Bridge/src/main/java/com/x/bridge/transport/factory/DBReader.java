package com.x.bridge.transport.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.mode.db.client.ClientWriteActor;
import com.x.bridge.transport.mode.db.client.IClientWriteActor;
import com.x.bridge.transport.mode.db.server.IServerWriteActor;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author AD
 * @date 2022/7/12 11:29
 */
public class DBReader implements IReader {


    private final IServerWriteActor clientReader;
    private final IClientWriteActor serverReader;
    private final IProxy proxy;

    private final LongAdder counter = new LongAdder();

    public DBReader(IProxy proxy) {
        this.proxy = proxy;
        this.clientReader = new ServerWriteActor();
        this.serverReader = new ClientWriteActor();
    }

    @Override
    public Message[] read() throws Exception {
        // 服务器模式
        if (proxy.isServerMode()) {
            return dynamicQuery(serverReader);
        } else {
            return dynamicQuery(clientReader);
        }
    }

    private Message[] dynamicQuery(IService<Message> reader) throws Exception {
        // 构建查询条件
        LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<>();
        query.eq(Message::getProxyServer, null);
        query.last("limit " + getLimit(counter.intValue()));
        try {
            List<Message> msgs = reader.list(query);
            counter.increment();
            if (counter.intValue() > Limit.high.ordinal()) {
                counter.decrement();
            }
            return msgs.toArray(new Message[0]);
        } catch (Exception e) {
            counter.decrement();
            if (counter.intValue() < Limit.low.ordinal()) {
                counter.reset();
                throw e;
            } else {
                dynamicQuery(reader);
            }
        }
        return new Message[0];
    }

    private int getLimit(int count) {
        Limit next = Limit.next(count);
        return next.limit;
    }

    private enum Limit {
        low(10),
        mediumLow(32),
        medium(64),
        mediumHigh(128),
        high(256);

        public static Limit next(int ordinal) {
            if (ordinal > high.ordinal()) {
                return high;
            } else if (ordinal < low.ordinal()) {
                return low;
            } else {
                for (Limit limit : values()) {
                    if (limit.ordinal() == ordinal) {
                        return limit;
                    }
                }
            }
            return medium;
        }

        private final int limit;

        private Limit(int limit) {
            this.limit = limit;
        }
    }
}
