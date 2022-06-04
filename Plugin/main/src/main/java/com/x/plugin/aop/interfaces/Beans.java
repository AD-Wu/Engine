package com.x.plugin.aop.interfaces;

/**
 * @author AD
 * @date 2022/5/23 22:48
 */
// @Configuration
public class Beans {

    // @Bean
    public PluginAdvisor pluginAdvisor() {
        PluginPointCut pointCut = new PluginPointCut();
        PluginAdvice advice = new PluginAdvice();
        PluginAdvisor advisor = new PluginAdvisor(pointCut);
        advisor.setAdvice(advice);
        return advisor;
    }
}
