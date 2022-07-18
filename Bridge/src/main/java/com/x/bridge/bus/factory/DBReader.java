package com.x.bridge.bus.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.x.bridge.bean.SessionMessage;
import com.x.bridge.dao.session.ISessionMessageDao;
import com.x.bridge.dao.session.SessionMessageDao;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.bus.core.IReader;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author AD
 * @date 2022/7/12 11:29
 */
public class DBReader implements IReader<SessionMessage> {

    private final ISessionMessageDao clientReader = new SessionMessageDao();

    private final ISessionMessageDao serverReader = new SessionMessageDao();

    private final IProxyService proxy;

    private final LongAdder counter = new LongAdder();

    public DBReader(IProxyService proxy) {
        this.proxy = proxy;
    }

    @Override
    public SessionMessage[] read() throws Exception {
        // 服务器模式
        if (proxy.isServerMode()) {
            SessionMessage[] msgs = dynamicQuery(serverReader);
            Set<Long> ids = Arrays.stream(msgs).map(m -> m.getId()).collect(Collectors.toSet());
            serverReader.removeBatchByIds(ids);
            return msgs;
        } else {
            SessionMessage[] msgs = dynamicQuery(clientReader);
            Set<Long> ids = Arrays.stream(msgs).map(m -> m.getId()).collect(Collectors.toSet());
            clientReader.removeBatchByIds(ids);
            return msgs;
        }
    }

    private SessionMessage[] dynamicQuery(IService<SessionMessage> reader) throws Exception {
        // 构建查询条件
        LambdaQueryWrapper<SessionMessage> query = new LambdaQueryWrapper<>();
        query.eq(SessionMessage::getProxyName, null);
        query.last("limit " + getLimit(counter.intValue()));
        try {
            List<SessionMessage> msgs = reader.list(query);
            counter.increment();
            if (counter.intValue() > LimitLevel.high.ordinal()) {
                counter.decrement();
            }
            return msgs.toArray(new SessionMessage[0]);
        } catch (Exception e) {
            counter.decrement();
            if (counter.intValue() < LimitLevel.low.ordinal()) {
                counter.reset();
                throw e;
            } else {
                dynamicQuery(reader);
            }
        }
        return new SessionMessage[0];
    }

    private int getLimit(int count) {
        LimitLevel next = LimitLevel.next(count);
        return next.limit;
    }

    private enum LimitLevel {
        low(10),
        mediumLow(32),
        medium(64),
        mediumHigh(128),
        high(256);

        public static LimitLevel next(int ordinal) {
            if (ordinal > high.ordinal()) {
                return high;
            } else if (ordinal < low.ordinal()) {
                return low;
            } else {
                for (LimitLevel limit : values()) {
                    if (limit.ordinal() == ordinal) {
                        return limit;
                    }
                }
            }
            return medium;
        }

        private final int limit;

        private LimitLevel(int limit) {
            this.limit = limit;
        }
    }

}
