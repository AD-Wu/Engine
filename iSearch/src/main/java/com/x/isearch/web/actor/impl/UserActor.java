package com.x.isearch.web.actor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.x.doraemon.common.web.Param;
import com.x.isearch.web.actor.base.IUserActor;
import com.x.isearch.web.dao.IUserDao;
import com.x.isearch.web.data.entity.User;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (User)表服务实现类
 *
 * @author AD
 * @since 2022-01-20 17:02:51
 */
@Service("userActor")
public class UserActor extends ServiceImpl<IUserDao, User> implements IUserActor {

    @Autowired
    private IUserDao dao;

    @Override
    public Page<User> get(Param<User> p) throws Exception {
        Page<User> page = new Page<>(p.getCurrent(), p.getSize());
        return page(page).setTotal(count());
    }

    @Override
    public Page<User> add(User user) throws Exception {
        save(user);
        Page<User> page = new Page<>(1, 10);
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.orderByDesc(User::getUpdateTime);
        return page(page, query).setTotal(count());
    }

    @Override
    public int delete(List<Serializable> ids) throws Exception {
        return dao.deleteBatchIds(ids);
    }

    @Override
    public int edit(User o) throws Exception {
        return dao.updateById(o);
    }
}

