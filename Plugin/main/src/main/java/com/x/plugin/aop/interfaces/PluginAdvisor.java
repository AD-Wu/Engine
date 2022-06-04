package com.x.plugin.aop.interfaces;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * @author AD
 * @date 2022/5/23 22:44
 */
public class PluginAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    private final Pointcut pointcut;

    public PluginAdvisor(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
