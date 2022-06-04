package com.x.isearch.web.data.ao;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * (User)参数类
 *
 * @author AD
 * @since 2022-01-20 16:46:31
 */
@Data
public class UserAo {

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

