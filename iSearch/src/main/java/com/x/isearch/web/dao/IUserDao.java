package com.x.isearch.web.dao;

import com.x.isearch.web.data.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (User)表数据库访问层
 *
 * @author AD
 * @since 2022-01-20 16:46:31
 */
@Mapper
@Repository
public interface IUserDao extends BaseMapper<User> {

}


