DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    ID          bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    NAME        varchar(30)     NOT NULL COMMENT '姓名',
    AGE         int             NULL DEFAULT NULL COMMENT '年龄',
    EMAIL       varchar(50)     NULL DEFAULT NULL COMMENT '邮箱',
    CREATE_TIME timestamp            DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：yyyy-MM-dd HH:mm:ss.SSS',
    UPDATE_TIME timestamp            DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：yyyy-MM-dd HH:mm:ss.SSS',
    PRIMARY KEY (ID)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1;

