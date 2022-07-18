package com.x.bridge.dao.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.bridge.bean.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 代理消息映射器
 *
 * @author AD
 * @since 2022-01-11 22:11:43
 */
@Mapper
@Repository
public interface IMessageMapper extends BaseMapper<Message> {

}


