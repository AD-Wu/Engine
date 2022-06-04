package com.x.plugin.aop.interfaces;

import com.x.plugin.anno.PluginParam;
import com.x.plugin.anno.RunPlugin;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author AD
 * @date 2022/5/23 22:34
 */
public class PluginAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Method method = mi.getMethod();
        RunPlugin plugin = method.getAnnotation(RunPlugin.class);
        if (plugin != null) {
            System.out.println(plugin.sceneNo());
            System.out.println(plugin.mgtOrgCode());
            System.out.println(plugin.pluginId());
        }else{
            Class<?> clazz = mi.getThis().getClass();
            System.out.println(clazz);
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                System.out.println(annotation);
            }
        }
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            PluginParam pp = parameter.getAnnotation(PluginParam.class);
            if (pp != null) {
                System.out.println("找到注解");
            }
        }

        return mi.proceed();
    }

}
