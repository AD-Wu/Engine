package com.x.bridge.transport;

import com.x.bridge.transport.factory.DBSender;
import com.x.bridge.transport.interfaces.ISender;
import com.x.bridge.transport.service.factory.MessageService;

/**
 * 传输管理者
 * @author AD
 * @date 2022/6/22 21:30
 */
public class SenderManager {

    public static ISender newSender(String sendMode){
        switch (sendMode){
            case "DB":
                return new DBSender(new MessageService());
            default:
                break;
        }
        return null;
    }
}
