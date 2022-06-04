package com.x.isearch.web.controller;


import com.x.doraemon.common.web.Param;
import com.x.doraemon.common.web.Result;
import com.x.isearch.web.actor.base.IProjectActor;
import com.x.isearch.web.data.ao.ProjectAo;
import com.x.isearch.web.data.entity.Project;
import com.x.isearch.web.data.vo.ProjectVo;
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
 * 项目管理
 *
 * @author AD
 * @since 2022-01-19 16:42:27
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    /**
     * 服务对象
     */
    @Resource
    private IProjectActor actor;


    /**
     * 查询
     *
     * @param param 分页查询参数
     * @return
     */
    @PostMapping("/get")
    public Result<List<Project>> get(@RequestBody @Valid Param<Project> param, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        return Result.success(actor.get(param));
    }

    /**
     * 新增
     *
     * @param ao
     * @return
     */
    @PostMapping("/add")
    public Result<Project> add(@RequestBody @Valid ProjectAo ao, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        Project proj = new Project();
        BeanUtils.copyProperties(ao,proj);
        return Result.success(actor.add(proj));
    }

    /**
     * 修改
     *
     * @param vo
     * @param binder  JSR-303校验结果
     * @return
     */
    @PostMapping("/edit")
    public Result<Project> edit(@RequestBody @Valid ProjectVo vo, BindingResult binder) throws Exception {
        if (binder.hasErrors()) {
            return getError(binder);
        }
        Project proj = new Project();
        BeanUtils.copyProperties(vo,proj);
        return Result.success(actor.edit(proj));
    }

    /**
     * 删除
     *
     * @param ids 主键数组
     * @return
     */
    @PostMapping("/delete")
    public Result<Integer> delete(@RequestBody(required = true) @Valid @Size(min = 1) Serializable[] ids, BindingResult binder)
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

