package com.x.demo.core.conf;

import com.x.plugin.aop.PluginAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author AD
 * @date 2022/5/28 8:34
 */
@Configuration
public class AopConfig {

    @Bean
    @ConditionalOnMissingBean
    public PluginAspect pluginAspect() {
        PluginAspect pa = new PluginAspect();
        return pa;
    }
}
