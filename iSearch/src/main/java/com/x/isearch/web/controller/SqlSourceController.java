package com.x.isearch.web.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.doraemon.common.web.Result;
import com.x.isearch.config.MyPage;
import com.x.isearch.web.actor.base.ISqlSourceActor;
import com.x.isearch.web.data.ao.SqlSourceAo;
import com.x.isearch.web.data.entity.SqlSource;
import com.x.isearch.web.util.ValidHelper;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * sql数据源管理
 *
 * @author AD
 * @since 2022-01-19 11:48:18
 */
@RestController
@RequestMapping("sqlDatasource")
public class SqlSourceController {

    /**
     * 服务对象
     */
    @Resource
    private ISqlSourceActor sourceService;

    /**
     * 查询分页数据
     *
     * @param page 分页对象
     * @return 所有数据
     */
    @GetMapping
    public Result<List<SqlSource>> selectAll(MyPage page) throws Exception {
        Page<SqlSource> p = new Page<>();
        p.setCurrent(page.getCurrent());
        p.setSize(page.getSize());
        Page<SqlSource> result = sourceService.page(p);
        List<SqlSource> records = result.getRecords();
        return Result.success(records);
    }

    /**
     * 查询单条数据
     *
     * @param id 主键|1
     * @return 单条数据
     */
    @GetMapping("{id}")
    public SqlSource selectOne(@PathVariable Serializable id) {
        return sourceService.getById(id);
    }

    /**
     * 新增数据
     *
     * @param source 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result<SqlSource> insert(@RequestBody SqlSourceAo source) throws Exception {
        try {
            SqlSource add = sourceService.add(source);
            return Result.success(add);
        } catch (Exception e) {
            return Result.fail(source).setMsg(e.getMessage());
        }

    }

    /**
     * 修改数据
     *
     * @param source 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result<SqlSource> update(@RequestBody @Valid SqlSource source, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            String error = ValidHelper.getError(result);
            return Result.fail(source).setMsg(error);
        }
        String errMsg = "更新失败";
        try {
            SqlSource update = sourceService.edit(source);
            return Result.success(update).setMsg("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            errMsg = e.getMessage();
        }
        return Result.fail(source).setMsg(errMsg);
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除结果
     */
    @DeleteMapping
    public Result<SqlSource> delete(@RequestParam("id") long id) throws Exception {
        SqlSource old = sourceService.getById(id);
        boolean delete = sourceService.delete(id);
        if (delete) {
            return Result.success(old).setMsg("删除成功");
        }
        if (old == null) {
            old = new SqlSource();
            old.setId(id);
            return Result.fail(old).setMsg("数据不存在");
        }
        return Result.fail(old).setMsg("删除失败");
    }
}

