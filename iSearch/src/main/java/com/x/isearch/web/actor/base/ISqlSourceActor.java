package com.x.isearch.web.actor.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.x.isearch.web.data.ao.SqlSourceAo;
import com.x.isearch.web.data.entity.SqlSource;

/**
 * (SqlDatasource)表服务接口
 *
 * @author AD
 * @since 2022-01-19 11:31:57
 */
public interface ISqlSourceActor extends IService<SqlSource> {

    SqlSource add(SqlSourceAo source) throws Exception;

    boolean delete(long id) throws Exception;

    SqlSource edit(SqlSource source) throws Exception;

}
