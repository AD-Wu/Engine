package com.x.isearch.web.data.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * (Project)实体类
 *
 * @author AD
 * @since 2022-01-19 16:42:27
 */
@Data
public class ProjectVo {

    /**
     * 项目主键
     *
     * @mock 1
     */
    @NotNull(message = "主键为空")
    private Long id;
    /**
     * 项目名
     *
     * @mock name
     */
    @NotBlank(message = "项目名为空")
    private String name;

    /**
     * 测试内容
     *
     * @mock c
     */
    private String text;


}

