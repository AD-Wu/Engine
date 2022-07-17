package com.x.bridge.dao.session;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.SessionMsg;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class SessionMsgDao extends ServiceImpl<ISessionMsgMapper, SessionMsg> implements ISessionMsgDao {

}

