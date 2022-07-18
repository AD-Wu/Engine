package com.x.bridge.dao.session;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.SessionMessage;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class SessionMessageDao extends ServiceImpl<ISessionMessageMapper, SessionMessage> implements ISessionMessageDao {

}

