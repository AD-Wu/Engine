package com.x.bridge.transport.factory;

import com.x.bridge.bean.Message;
import com.x.bridge.proxy.conf.ProxyConfig;
import com.x.bridge.transport.interfaces.ISender;
import com.x.bridge.transport.service.interfaces.IMessageService;
import java.util.Arrays;

/**
 * 数据库传送
 * @author AD
 * @date 2022/6/25 13:05
 */
public class DBSender implements ISender<Message> {

    private IMessageService msgService;

    public DBSender(IMessageService msgService) {
        this.msgService = msgService;
    }

    @Override
    public void send(Message... msgs) {
        if (msgs != null || msgs.length > 0) {
            msgService.saveBatch(Arrays.asList(msgs));
        }
    }

    @Override
    public void start(ProxyConfig conf) {

    }
}
