package com.x.bridge.dao.msg;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.bridge.bean.Msg;
import org.springframework.stereotype.Service;

/**
 * @author AD
 * @since 2022-01-11 22:11:45
 */
@Service
public class MsgDao extends ServiceImpl<IMsgMapper, Msg> implements IMsgDao {

}

