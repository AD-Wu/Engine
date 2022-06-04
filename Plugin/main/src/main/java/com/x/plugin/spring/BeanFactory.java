package com.x.plugin.spring;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/5/28 17:27
 */
@Component
public class BeanFactory {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private static ConfigurableListableBeanFactory beans;

    @PostConstruct
    private void init() {
        beans = beanFactory;
    }

    public static boolean containsBean(String name) {
        return beans.containsBean(name);
    }

    public static Object getBean(String name) {
        return beans.getBean(name);
    }


}
