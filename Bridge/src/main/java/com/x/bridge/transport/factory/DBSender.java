package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.transport.interfaces.ISender;
import com.x.bridge.transport.mode.db.interfaces.IMessageService;
import java.util.Arrays;

/**
 * 数据库传送
 * @author AD
 * @date 2022/6/25 13:05
 */
public class DBSender implements ISender<Message> {

    private IMessageService sender;

    public DBSender(IMessageService sender) {
        this.sender = sender;
    }

    @Override
    public void send(Message... msgs) {
        if (msgs != null || msgs.length > 0) {
            sender.saveBatch(Arrays.asList(msgs));
        }
    }

}
