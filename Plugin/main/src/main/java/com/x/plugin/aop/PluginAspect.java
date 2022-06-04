package com.x.plugin.aop;

import com.x.plugin.anno.RunPlugin;
import com.x.plugin.core.IBeanGetter;
import com.x.plugin.core.IPluginManager;
import com.x.plugin.facotry.DefaultBeanGetter;
import com.x.plugin.spring.BeanFactory;
import com.x.plugin.spring.PluginIniter;
import com.x.plugin.spring.PluginManagerFactory;
import com.x.plugin.util.StringHelper;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/5/17 20:12
 */
@Aspect
public class PluginAspect {

    private static final Logger LOG = LoggerFactory.getLogger(PluginAspect.class);

    private final IBeanGetter beanGetter;

    public PluginAspect() {
        this(new DefaultBeanGetter());
    }

    public PluginAspect(IBeanGetter beanGetter) {
        this.beanGetter = beanGetter;
    }

    @Pointcut("@annotation(com.x.plugin.anno.RunPlugin)")
    public void plugin() {}

    @Before("plugin()")
    public void before(JoinPoint jp) throws Throwable {
        // System.out.println("before");
    }

    @AfterReturning(returning = "ret", pointcut = "plugin()")
    public void afterReturning(Object ret) throws Throwable {
        // System.out.println("afterReturning");
    }

    @After("plugin()")
    public void after() throws Throwable {
        // System.out.println("after");
    }

    @AfterThrowing(throwing = "e", pointcut = "plugin()")
    public void afterThrowing(JoinPoint jp, Exception e) throws Throwable {
        // System.out.println("afterThrowing");
    }

    @Around("plugin()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        RunPluginData data = new RunPluginData(jp);
        RunPlugin plugin = data.getRunPlugin();
        Object pluginParam = data.getPluginParam();
        LOG.info("当前调用,类:【{}】,方法:【{}】,插件注解:【sceneNo={}, mgtOrgCode={}, pluginId={}】",
                 data.getClazz(),
                 data.getMethod().getName(),
                 plugin.sceneNo(),
                 plugin.mgtOrgCode(),
                 plugin.pluginId());
        if (beanGetter == null) {
            LOG.info("IBeanGetter为空");
            throw new Exception("IBeanGetter为空");
        }
        String pluginId = beanGetter.getPluginId(plugin, pluginParam);
        String beanName = beanGetter.getBeanName(plugin, pluginParam);
        LOG.info("Bean获取器:【{}】取得: 插件id【{}】, bean名称【{}】", beanGetter.getClass().getName(), pluginId, beanName);
        IPluginManager manager = PluginManagerFactory.getPluginManager(pluginId);
        Object bean = null;
        if (manager == null) {
            LOG.warn("不存在插件:【{}】", pluginId);
            // 插件id为空时,从bean工常获取
            if (StringHelper.isNull(pluginId)) {
                // 直接获取bean(插件工程可能作为主工程debug启动)
                if (BeanFactory.containsBean(beanName)) {
                    bean = BeanFactory.getBean(beanName);
                    LOG.info("从【{}】获取Bean:【{}】, 类:【{}】", "本应用", beanName, bean.getClass());
                }
            }
        } else {
            // 从插件工程中获取bean
            if (manager.containsBean(beanName)) {
                bean = manager.getBean(beanName);
                LOG.info("从【{}】获取Bean:【{}】, 类:【{}】", "插件应用", beanName, bean.getClass());
            }
        }
        if (bean == null) {
            LOG.warn("不存在Bean:【{}】", beanName);
        } else {
            // 要校验当前bean和
            Class<?> beanClass = bean.getClass();
            String name = data.getMethod().getName();
            Class<?>[] argClasses = data.getArgClasses();
            Method method = null;
            try {
                method = beanClass.getMethod(name, argClasses);
            } catch (NoSuchMethodException e) {
                LOG.error("类:【{}】不存在方法:【{}】,请检查是否与类:【{}】存在同一个接口", beanClass, name, data.getClazz());
                throw new Exception("方法不存在,请检查是否存在同一个接口");
            }
            if (method != null) {
                method.setAccessible(true);
                Object result = method.invoke(bean, data.getArgs());
                LOG.info("拦截并执行: 类【{}】,方法【{}】", beanClass, name);
                LOG.info(PluginIniter.splitLine);
                return result;
            }
        }
        // 原方法
        Object result = jp.proceed(data.getArgs());
        LOG.info("默认执行:类【{}】,方法:【{}】", data.getClazz(), data.getMethod().getName());
        LOG.info(PluginIniter.splitLine);
        return result;
    }

}
