package com.x.datago.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * (User)实体类
 *
 * @author AD
 * @since 2022-01-11 22:11:44
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 邮箱
     */
    private String email;


}

