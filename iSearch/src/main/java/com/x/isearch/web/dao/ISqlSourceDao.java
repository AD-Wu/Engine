package com.x.isearch.web.dao;

import com.x.isearch.web.data.entity.SqlSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (SqlDatasource)表数据库访问层
 *
 * @author AD
 * @since 2022-01-19 11:31:55
 */
@Mapper
@Repository
public interface ISqlSourceDao extends BaseMapper<SqlSource> {

}


