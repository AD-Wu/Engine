package com.x.bridge.dao.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.bridge.bean.SessionMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author AD
 * @since 2022-01-11 22:11:43
 */
@Mapper
@Repository
public interface ISessionMessageMapper extends BaseMapper<SessionMessage> {

}


