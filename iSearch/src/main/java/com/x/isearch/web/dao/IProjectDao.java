package com.x.isearch.web.dao;

import com.x.isearch.web.data.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Project)表数据库访问层
 *
 * @author AD
 * @since 2022-01-19 16:42:27
 */
@Mapper
@Repository
public interface IProjectDao extends BaseMapper<Project> {

}


