package com.x.bridge.dao.message;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.Message;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class MessageDao extends ServiceImpl<IMessageMapper, Message> implements IMessageDao {

}

