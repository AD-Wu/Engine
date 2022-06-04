package com.x.plugin.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件注解,用于替换@Autoware
 * @author AD
 * @date 2022/5/17 19:55
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RunPlugin {

    /**
     * 场景编号
     * @return
     */
    String sceneNo();

    /**
     * 供电单位编号
     * @return
     */
    String mgtOrgCode();

    /**
     * 插件标识
     * @return
     */
    String pluginId() default "";
}
