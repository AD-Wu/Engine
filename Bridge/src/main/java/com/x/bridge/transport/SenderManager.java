package com.x.bridge.transport;

import com.x.bridge.transport.factory.DBWriter;
import com.x.bridge.transport.interfaces.IWriter;
import com.x.bridge.transport.mode.db.server.ServerWriteActor;

/**
 * 传输管理者
 * @author AD
 * @date 2022/6/22 21:30
 */
public class SenderManager {

    public static IWriter newSender(String sendMode){
        switch (sendMode){
            case "DB":
                return new DBWriter(new ServerWriteActor());
            default:
                break;
        }
        return null;
    }
}
