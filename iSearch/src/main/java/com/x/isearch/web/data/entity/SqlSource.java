package com.x.isearch.web.data.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * (SqlDatasource)实体类
 *
 * @author AD
 * @since 2022-01-19 12:07:34
 */
@Data
public class SqlSource implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     *
     * @mock 1
     */
    @TableId(type = IdType.AUTO)

    private Long id;
    /**
     * 数据源名称,唯一
     *
     * @mock xxx数据源
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY, insertStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "数据源名称为空")
    private String name;
    /**
     * 数据库路径
     *
     * @mock jdbc:mysql://localhost:3306/isearch?serverTimezone=UTC&useSSL=false
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY, insertStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "URL地址为空")
    private String url;
    /**
     * 驱动类名
     *
     * @mock com.mysql.cj.jdbc.Driver
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY, insertStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "驱动类名为空")
    private String driver;
    /**
     * 用户
     *
     * @mock root
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY, insertStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "用户名为空")
    private String user;
    /**
     * 密码
     *
     * @mock 123456
     */
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY, insertStrategy = FieldStrategy.NOT_EMPTY)
    @NotBlank(message = "密码为空")
    private String pwd;

}

