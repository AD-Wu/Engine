package com.x.isearch.web.actor.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.isearch.web.actor.base.ISqlSourceActor;
import com.x.isearch.web.common.Where;
import com.x.isearch.web.dao.ISqlSourceDao;
import com.x.isearch.web.data.ao.SqlSourceAo;
import com.x.isearch.web.data.entity.SqlSource;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * (SqlDatasource)表服务实现类
 *
 * @author AD
 * @since 2022-01-19 11:31:58
 */
@Service
public class SqlSourceActor extends ServiceImpl<ISqlSourceDao, SqlSource> implements ISqlSourceActor {

    @Override
    public SqlSource add(SqlSourceAo source) throws Exception {
        // 判断是否有重复
        Where<SqlSource> where = Where.get(SqlSource.class);
        where.eq(SqlSource::getName, source.getName());
        SqlSource old = getOne(where);
        if (old != null) {
            throw new RuntimeException("数据源名重复");
        }
        SqlSource target = new SqlSource();
        BeanUtils.copyProperties(source, target);
        save(target);
        return getOne(where);
    }

    @Override
    public boolean delete(long id) throws Exception {
        return  removeById(id);
    }

    @Override
    public SqlSource edit(SqlSource source) throws Exception {
        // 判断数据源名是否有重复
        Where<SqlSource> where = Where.get(SqlSource.class);
        where.eq(SqlSource::getName, source.getName());
        where.notIn(SqlSource::getId, source.getId());
        List<SqlSource> sources = list(where);
        if (CollectionUtils.isEmpty(sources)) {
            updateById(source);
            return getById(source.getId());
        } else {
            throw new RuntimeException("数据源名重复");
        }
    }
}

