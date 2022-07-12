package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.transport.interfaces.IWriter;
import com.x.bridge.transport.mode.db.client.ClientWriteActor;
import com.x.bridge.transport.mode.db.client.IClientWriteActor;
import com.x.bridge.transport.mode.db.server.IServerWriteActor;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;
import java.util.Arrays;

/**
 * 数据库传送
 * @author AD
 * @date 2022/6/25 13:05
 */
public class DBWriter implements IWriter<Message> {

    private IServerWriteActor serverWriter;
    private IClientWriteActor clientWriter;
    private boolean serverMode;

    public DBWriter(boolean serverMode) {
        this.serverMode = serverMode;
        this.serverWriter = new ServerWriteActor();
        this.clientWriter = new ClientWriteActor();

    }

    @Override
    public void write(Message... msgs) throws Exception{
        if (msgs != null || msgs.length > 0) {
            if (serverMode) {
                serverWriter.saveBatch(Arrays.asList(msgs));
            } else {
                clientWriter.saveBatch(Arrays.asList(msgs));
            }
        }
    }

}
