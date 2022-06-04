package com.x.isearch.web.actor.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.x.doraemon.common.web.Param;
import java.io.Serializable;
import java.util.List;
import com.x.isearch.web.data.entity.User;

/**
 * (User)表服务接口
 *
 * @author AD
 * @since 2022-01-20 16:46:32
 */
public interface IUserActor extends IService<User> {

    Page<User> get(Param<User> param) throws Exception;

    Page<User> add(User user) throws Exception;

    int delete(List<Serializable>ids) throws Exception;

    int edit(User user) throws Exception;
}
