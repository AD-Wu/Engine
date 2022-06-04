package com.x.plugin.aop;

import com.x.plugin.anno.PluginParam;
import com.x.plugin.anno.RunPlugin;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author AD
 * @date 2022/5/18 11:07
 */
public final class RunPluginData {

    private final Object target;
    private final Class<?> clazz;
    private final MethodSignature signature;
    private final Method method;
    private final RunPlugin runPlugin;
    private final Object[] args;
    private final Class<?>[] argClasses;
    private final Class<?> returnType;
    private Object pluginParam;


    public RunPluginData(ProceedingJoinPoint jp) throws NoSuchMethodException {
        // 类对象
        this.target = jp.getTarget();
        // 类
        this.clazz = target.getClass();
        // 方法签名
        this.signature = (MethodSignature) jp.getSignature();
        // 方法
        this.method = signature.getMethod();
        // 方法上的注解
        this.runPlugin = method.getAnnotation(RunPlugin.class);
        // 方法入参(为空时类型将获取不到)
        this.args = jp.getArgs();
        // 标注@PluginParam的参数
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            PluginParam pp = param.getAnnotation(PluginParam.class);
            if (pp != null) {
                this.pluginParam = args[i];
                break;
            }
        }
        // 方法类型
        this.argClasses = signature.getParameterTypes();
        // 方法返回类型
        this.returnType = signature.getReturnType();


    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Signature getSignature() {
        return signature;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class<?>[] getArgClasses() {
        return argClasses;
    }

    public Method getMethod() {
        return method;
    }

    public RunPlugin getRunPlugin() {
        return runPlugin;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Object getPluginParam() {return pluginParam;}
}
