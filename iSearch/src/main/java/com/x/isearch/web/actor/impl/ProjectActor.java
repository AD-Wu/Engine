package com.x.isearch.web.actor.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.doraemon.Strings;
import com.x.doraemon.common.web.Param;
import com.x.isearch.web.actor.base.IProjectActor;
import com.x.isearch.web.common.Where;
import com.x.isearch.web.dao.IProjectDao;
import com.x.isearch.web.data.entity.Project;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (Project)表服务实现类
 *
 * @author AD
 * @since 2022-01-19 16:42:28
 */
@Service
public class ProjectActor extends ServiceImpl<IProjectDao, Project> implements IProjectActor {

    @Autowired
    private IProjectDao pdao;

    @Override
    public List<Project> get(Param<Project> p) throws Exception {
        Page<Project> page = new Page<>(p.getCurrent(), p.getSize());
        Where<Project> where = Where.get(Project.class);
        Project data = p.getData();
        if (data != null) {
            where.eq(Objects.nonNull(data.getId()), Project::getId, data.getId());
            where.eq(Strings.isNotBlank(data.getName()), Project::getName, data.getName());
            where.like(Strings.isNotBlank(data.getText()), Project::getText, data.getText());
        }
        return page(page, where).getRecords();
    }

    @Override
    public Project add(Project proj) throws Exception {
        // 判断是否已存在项目名
        Where<Project> where = Where.get(Project.class);
        where.eq(Project::getName, proj.getName());
        if (getOne(where) != null) {
            throw new RuntimeException("项目名重复");
        }
        save(proj);
        return getOne(where);
    }

    @Override
    public int delete(Serializable[] ids) throws Exception {
        Where<Project> where = Where.get(Project.class);
        where.in(Project::getId, ids);
        return pdao.delete(where);
    }

    @Override
    public int edit(Project proj) throws Exception {
        // 判断项目名是否已存在
        Where<Project> where = Where.get(Project.class);
        where.eq(Project::getName, proj.getName());
        where.notIn(Project::getId, proj.getId());
        if (getOne(where) != null) {
            throw new RuntimeException("项目名重复");
        }
        return pdao.updateById(proj);
    }


}

