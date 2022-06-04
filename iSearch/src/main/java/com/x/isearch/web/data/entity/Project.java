package com.x.isearch.web.data.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * (Project)实体类
 *
 * @author AD
 * @since 2022-01-19 16:42:27
 */
@Data
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 项目主键
     *
     * @mock 1
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 项目名
     *
     * @mock name
     */
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String name;

    /**
     * 测试内容
     *
     * @mock c
     */
    private String text;


}

