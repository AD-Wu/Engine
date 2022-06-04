package com.x.plugin.aop.interfaces;

import com.x.plugin.anno.RunPlugin;
import java.lang.reflect.Method;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author AD
 * @date 2022/5/23 22:15
 */
public class PluginPointCut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 查询方法上的注解,包括查找父类
        boolean has = AnnotatedElementUtils.hasAnnotation(method, RunPlugin.class);
        if (has) {
            System.out.println(method.getDeclaringClass().getName() + " : " + method.getName());
        }
        return has;
    }
}
