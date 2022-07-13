package com.x.bridge.transport;

import com.x.bridge.transport.interfaces.IWriter;

/**
 * 传输管理者
 * @author AD
 * @date 2022/6/22 21:30
 */
public class SenderManager {

    public static IWriter newSender(String sendMode){
        switch (sendMode){
            case "DB":
                // return new DBWriter(new ServerWriteActor());
            default:
                break;
        }
        return null;
    }
}
