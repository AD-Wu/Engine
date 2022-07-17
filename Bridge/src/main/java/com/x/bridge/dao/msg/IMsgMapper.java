package com.x.bridge.dao.msg;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.bridge.bean.Msg;
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
public interface IMsgMapper extends BaseMapper<Msg> {

}


