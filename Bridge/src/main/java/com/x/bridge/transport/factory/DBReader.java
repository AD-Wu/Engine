package com.x.bridge.transport.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.x.bridge.bean.SessionMsg;
import com.x.bridge.dao.session.ISessionMsgDao;
import com.x.bridge.dao.session.SessionMsgDao;
import com.x.bridge.proxy.core.IProxyService;
import com.x.bridge.transport.core.IReader;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author AD
 * @date 2022/7/12 11:29
 */
public class DBReader implements IReader<SessionMsg> {
    
    private final ISessionMsgDao clientReader = new SessionMsgDao();
    
    private final ISessionMsgDao serverReader = new SessionMsgDao();
    
    private final IProxyService proxy;
    
    private final LongAdder counter = new LongAdder();
    
    public DBReader(IProxyService proxy) {
        this.proxy = proxy;
    }
    
    @Override
    public SessionMsg[] read() throws Exception {
        // 服务器模式
        if (proxy.isServerMode()) {
            SessionMsg[] msgs = dynamicQuery(serverReader);
            Set<Long> ids = Arrays.stream(msgs).map(m -> m.getId()).collect(Collectors.toSet());
            serverReader.removeBatchByIds(ids);
            return msgs;
        } else {
            SessionMsg[] msgs = dynamicQuery(clientReader);
            Set<Long> ids = Arrays.stream(msgs).map(m -> m.getId()).collect(Collectors.toSet());
            clientReader.removeBatchByIds(ids);
            return msgs;
        }
    }
    
    private SessionMsg[] dynamicQuery(IService<SessionMsg> reader) throws Exception {
        // 构建查询条件
        LambdaQueryWrapper<SessionMsg> query = new LambdaQueryWrapper<>();
        query.eq(SessionMsg::getProxyName, null);
        query.last("limit " + getLimit(counter.intValue()));
        try {
            List<SessionMsg> msgs = reader.list(query);
            counter.increment();
            if (counter.intValue() > LimitLevel.high.ordinal()) {
                counter.decrement();
            }
            return msgs.toArray(new SessionMsg[0]);
        } catch (Exception e) {
            counter.decrement();
            if (counter.intValue() < LimitLevel.low.ordinal()) {
                counter.reset();
                throw e;
            } else {
                dynamicQuery(reader);
            }
        }
        return new SessionMsg[0];
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
