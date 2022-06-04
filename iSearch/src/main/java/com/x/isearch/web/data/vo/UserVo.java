package com.x.isearch.web.data.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * (User)参数类
 *
 * @author AD
 * @since 2022-01-20 16:46:32
 */
@Data
public class UserVo {

    /**
     * 主键ID
     */
    @NotNull(message = "主键为空")
    private Long id;
    /**
     * 姓名
     */
    @NotEmpty(message = "姓名为空")
    private String name;
    /**
     * 年龄
     */
    @Range(min = 1, max = 150, message = "年龄范围[1,150]")
    private Integer age;
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

}

