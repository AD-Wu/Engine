package com.x.isearch.web.data.ao;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * (SqlDatasource)实体类
 *
 * @author AD
 * @since 2022-01-19 12:07:34
 */
@Data
public class SqlSourceAo {

    /**
     * 主键
     * @ignore
     */
    private Long id;
    /**
     * 数据源名称,唯一
     * @mock sunday-isearch
     */
    @NotNull(message = "主键不能为空")
    private String name;
    /**
     * 数据库路径
     * @mock jdbc:mysql://localhost:3306/isearch?serverTimezone=UTC&useSSL=false
     */
    private String url;
    /**
     * 驱动类名
     * @mock com.mysql.cj.jdbc.Driver
     */
    private String driver;
    /**
     * 用户
     * @mock root
     */
    private String user;
    /**
     * 密码
     * @mock 123456
     */
    private String pwd;

}

