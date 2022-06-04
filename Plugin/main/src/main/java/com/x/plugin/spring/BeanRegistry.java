package com.x.plugin.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.UrlResource;

/**
 * @author AD
 * @date 2022/5/7 21:25
 */
final class BeanRegistry {

    private static final Logger logger = LoggerFactory.getLogger(BeanRegistry.class);

    private final Set<BeanDefinitionHolder> holders;
    private final BeanDefinitionRegistry registry;

    BeanRegistry(BeanDefinitionRegistry registry, Set<BeanDefinitionHolder> holders) {
        this.registry = registry;
        this.holders = holders;
    }

    final int register() {
        Map<String, String> urlName = new HashMap<>();
        Map<String, Integer> nameCount = new HashMap<>();
        int total = 0;
        for (BeanDefinitionHolder holder : holders) {
            BeanDefinition def = holder.getBeanDefinition();
            if (def instanceof ScannedGenericBeanDefinition) {
                // 注册到工厂
                registry.registerBeanDefinition(holder.getBeanName(), def);
                total++;
                // 记录日志
                log(holder, urlName, nameCount);
            }
        }
        return total;
    }

    private void log(BeanDefinitionHolder holder, Map<String, String> urlName, Map<String, Integer> nameCount) {
        BeanDefinition bean = holder.getBeanDefinition();
        Object source = bean.getSource();
        if (source instanceof UrlResource) {
            String classUrl = ((UrlResource) source).getURL().toString();
            String jarUrl = classUrl.substring(0, classUrl.indexOf("!/"));
            if (urlName.containsKey(jarUrl)) {
                String pluginName = urlName.get(jarUrl);
                int count = nameCount.get(pluginName);
                nameCount.put(pluginName, ++count);
            } else {
                int count = 0;
                int start = jarUrl.lastIndexOf("/");
                String pluginName = jarUrl.substring(start + 1);
                urlName.put(jarUrl, pluginName);
                nameCount.put(pluginName, ++count);
            }
            String pluginName = urlName.get(jarUrl);
            int count = nameCount.get(pluginName);
            logger.info("\t插件【{}】注册第【{}】个Bean【{}】-【{}】", pluginName, count, holder.getBeanName(), bean.getBeanClassName());
        }
    }
}
