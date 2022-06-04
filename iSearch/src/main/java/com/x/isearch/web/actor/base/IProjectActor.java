package com.x.isearch.web.actor.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.x.doraemon.common.web.Param;
import com.x.isearch.web.data.entity.Project;
import java.io.Serializable;
import java.util.List;

/**
 * (Project)表服务接口
 *
 * @author AD
 * @since 2022-01-19 16:42:28
 */
public interface IProjectActor extends IService<Project> {

    List<Project> get(Param<Project> param) throws Exception;

    Project add(Project proj) throws Exception;

    int delete(Serializable[] ids) throws Exception;

    int edit(Project proj) throws Exception;
}
