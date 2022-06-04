package com.x.isearch.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.doraemon.common.web.Param;
import com.x.doraemon.common.web.Result;
import com.x.isearch.web.actor.base.IUserActor;
import com.x.isearch.web.data.ao.UserAo;
import com.x.isearch.web.data.entity.User;
import com.x.isearch.web.data.vo.UserVo;
import com.x.isearch.web.util.ValidHelper;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 *
 * @author AD
 * @since 2022-01-20 17:02:50
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 服务对象
     */
    @Resource
    private IUserActor actor;

    /**
     * 查询
     *
     * @param param 分页查询参数
     * @return 分页数据
     */
    @PostMapping("/get")
    public Result<List<User>> get(@RequestBody @Valid Param<User> param, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        Page<User> page = actor.get(param);
        Result r = Result.success(page.getRecords());
        return r;
    }

    /**
     * 新增
     *
     * @param ao 入参对象
     * @return 新增结果
     */
    @PostMapping("/add")
    public Result<Integer> add(@RequestBody @Valid UserAo ao, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        User entity = new User();
        BeanUtils.copyProperties(ao, entity);
        Page<User> page = actor.add(entity);
        Result r = Result.success(page.getRecords());
        return r;
    }

    /**
     * 修改
     *
     * @param vo     入参对象
     * @param binder JSR-303校验结果
     * @return 修改结果
     */
    @PostMapping("/edit")
    public Result<User> edit(@RequestBody @Valid UserVo vo, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        User entity = new User();
        BeanUtils.copyProperties(vo, entity);
        return Result.success(actor.edit(entity));
    }

    /**
     * 删除
     *
     * @param ids 主键数组
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result<Integer> delete(@RequestBody(required = true) @Valid @Size(min = 1) List<Serializable> ids, BindingResult binder)
        throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        return Result.success(actor.delete(ids));
    }

    private Result getError(BindingResult binder) {
        String error = ValidHelper.getError(binder);
        return Result.fail().setMsg(error);
    }
}

