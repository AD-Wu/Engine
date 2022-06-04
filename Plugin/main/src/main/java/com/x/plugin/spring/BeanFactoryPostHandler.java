package com.x.plugin.spring;

import com.x.plugin.core.IPluginManager;
import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginData;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/5/11 18:44
 */
@Component
public final class BeanFactoryPostHandler implements BeanDefinitionRegistryPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BeanFactoryPostHandler.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        IPluginManager[] managers = PluginManagerFactory.getPluginManagers();
        Map<String, BeanDefinition> defs = PluginManagerFactory.getBeanDefinitions();
        for (IPluginManager manager : managers) {
            PluginData[] datas = manager.getPluginDatas();
            for (PluginData data : datas) {
                BeanData[] beans = data.getBeans();
                for (BeanData bean : beans) {
                    // 改造后的bean名称
                    String name = bean.getName();
                    if (registry.containsBeanDefinition(name)) {
                        throw new BeanDefinitionStoreException("来自【" + data.getEnvId() + "】环境的bean:【" + name + "】已存在");
                    }
                    BeanDefinition def = defs.get(name);
                    registry.registerBeanDefinition(name, def);
                }
            }
        }
        LOG.info("注册Bean成功,共【{}】个", defs.size());
        LOG.info(PluginIniter.splitLine);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        PluginManager.setBeanFactory(beanFactory);
    }

}
