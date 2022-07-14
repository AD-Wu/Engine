package com.x.bridge.transport.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.bridge.bean.Message;
import com.x.bridge.proxy.core.IProxy;
import com.x.bridge.transport.core.IReader;
import com.x.bridge.transport.mode.db.client.ClientWriteActor;
import com.x.bridge.transport.mode.db.client.IClientWriteActor;
import com.x.bridge.transport.mode.db.server.IServerWriteActor;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;
import java.util.List;

/**
 * @author AD
 * @date 2022/7/12 11:29
 */
public class DBReader implements IReader{

    private IServerWriteActor clientReader;
    private IClientWriteActor serverReader;
    private IProxy proxy;

    public DBReader(IProxy proxy) {
        this.proxy = proxy;
        this.clientReader = new ServerWriteActor();
        this.serverReader = new ClientWriteActor();
    }

    @Override
    public Message[] read() throws Exception{
        List<Message> msgs;
        if (proxy.isServerMode()) {
            LambdaQueryWrapper<Message> query = new LambdaQueryWrapper<>();
            query.eq(Message::getProxyServer,null);

            msgs = serverReader.list(query);
        } else {
            msgs = clientReader.list();
        }
        return msgs.toArray(new Message[0]);
    }
}
