package com.x.bridge.transport.service.factory;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.Message;
import com.x.bridge.transport.service.dao.IMessageDao;
import com.x.bridge.transport.service.interfaces.IMessageService;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class MessageService extends ServiceImpl<IMessageDao, Message> implements IMessageService {

}

