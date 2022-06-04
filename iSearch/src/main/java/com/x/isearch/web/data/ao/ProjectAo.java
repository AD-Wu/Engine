package com.x.isearch.web.data.ao;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * (Project)参数类
 *
 * @author AD
 * @since 2022-01-19 16:42:27
 */
@Data
public class ProjectAo {
    /**
     * 项目主键
     * @mock null
     */
    private Long id;
    /**
     * 项目名
     * @mock pname
     */
    @NotBlank(message = "项目名为空")
    private String name;

    /**
     * 测试内容
     * @mock c
     */
    private String text;

}

