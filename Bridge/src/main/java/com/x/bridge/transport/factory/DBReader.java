package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.transport.interfaces.IReader;
import com.x.bridge.transport.mode.db.client.ClientWriteActor;
import com.x.bridge.transport.mode.db.client.IClientWriteActor;
import com.x.bridge.transport.mode.db.server.IServerWriteActor;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;
import java.util.List;

/**
 * @author AD
 * @date 2022/7/12 11:29
 */
public class DBReader implements IReader<Message> {

    private IServerWriteActor clientReader;
    private IClientWriteActor serverReader;
    private boolean serverMode;

    public DBReader(boolean serverMode) {
        this.serverMode = serverMode;
        this.clientReader = new ServerWriteActor();
        this.serverReader = new ClientWriteActor();
    }

    @Override
    public Message[] read() throws Exception{
        List<Message> msgs;
        if (serverMode) {
            msgs = serverReader.list();
        } else {
            msgs = clientReader.list();
        }
        return msgs.toArray(new Message[0]);
    }
}
