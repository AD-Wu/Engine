package com.x.bridge.dao.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.bridge.bean.SessionMsg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (User)表数据库访问层
 *
 * @author AD
 * @since 2022-01-11 22:11:43
 */
@Mapper
@Repository
public interface ISessionMsgMapper extends BaseMapper<SessionMsg> {

}


