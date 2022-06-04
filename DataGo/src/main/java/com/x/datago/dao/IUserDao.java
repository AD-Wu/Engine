package com.x.datago.dao;

import com.x.datago.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface IUserDao extends BaseMapper<User> {

}


