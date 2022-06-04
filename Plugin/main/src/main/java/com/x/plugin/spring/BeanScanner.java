package com.x.plugin.spring;

import com.x.plugin.anno.NoModify;
import com.x.plugin.core.IPlugin;
import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginData;
import com.x.plugin.spring.filter.exclude.SpringbootAppFilter;
import com.x.plugin.util.StringHelper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * @author AD
 * @date 2022/4/24 9:49
 */
class BeanScanner extends ClassPathScanningCandidateComponentProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanScanner.class);

    private static final TypeFilter excludeFilter = new SpringbootAppFilter();

    private static final BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    private static final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private static final MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

    private String[] autowireCandidatePatterns;

    private final String pattern = "**/*.class";

    private final BeanDefinitionRegistry registry;

    private final BeanDefinitionDefaults beanDefDefaults;

    private final IPlugin[] plugins;

    private final Set<String> packages;

    private final Set<String> excludeClassNames;

    BeanScanner(BeanDefinitionRegistry registry, IPlugin[] plugins, Set<String> packages, Set<String> excludeClassNames) {
        this.plugins = plugins;
        this.registry = registry;
        this.packages = packages;
        this.excludeClassNames = excludeClassNames;
        this.beanDefDefaults = new BeanDefinitionDefaults();
        registerDefaultFilters();
        // 设置exclude过滤器,排查插件包中的启动类
        addExcludeFilter(excludeFilter);
    }

    public PluginData[] scan() throws Exception {
        // 获取jar包在文件系统中的绝对路径
        Map<URL, IPlugin> pluginPathMap = Arrays.stream(plugins)
            .collect(Collectors.toMap(p -> p.url(), p -> p, (existingValue, newValue) -> existingValue, LinkedHashMap::new));
        return scan(pluginPathMap);
    }

    private PluginData[] scan(Map<URL, IPlugin> pluginPathMap) throws Exception {
        if (pluginPathMap == null || pluginPathMap.size() < 1) {
            logger.error("没有插件信息,不进行Bean扫描");
            return new PluginData[0];
        }
        Iterator<Entry<URL, IPlugin>> it = pluginPathMap.entrySet().iterator();
        List<PluginData> pds = new ArrayList<>();
        while (it.hasNext()) {
            Entry<URL, IPlugin> next = it.next();
            URL jarURL = next.getKey();
            IPlugin plugin = next.getValue();
            if (StringHelper.isNull(jarURL.toString())) {
                LOGGER.error("jar包路径不合法:{}", jarURL);
                throw new Exception("jar包路径不合法:" + jarURL);
            }
            // 生成插件数据
            PluginData pd = createPluginData(plugin);
            pds.add(pd);
            // 设置插件bean容器
            List<BeanData> beanDatas = new ArrayList<>();
            // 构建候选BeanDefinition并遍历
            List<AnnotatedBeanDefinition> defs = findCandidate(jarURL);
            for (AnnotatedBeanDefinition def : defs) {
                // 作用域
                ScopeMetadata sm = this.scopeMetadataResolver.resolveScopeMetadata(def);
                def.setScope(sm.getScopeName());
                // 实际bean名称
                String srcName = this.beanNameGenerator.generateBeanName(def, this.registry);
                // 环境id
                String envId = plugin.envId();
                // 改造bean名称
                String name = PluginManagerFactory.beanNameModifier.modify(envId, srcName);
                // bean名称是否修改
                boolean modify = true;
                // 获取bean上的注解
                AnnotationMetadata metadata = def.getMetadata();
                // bean名称不进行修改(注解@NoModify表示bean名称不进行插件化改造)
                if (metadata.hasAnnotation(NoModify.class.getName())) {
                    name = srcName;
                    modify = false;
                }
                if (def instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) def, name);
                }
                if (def instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) def);
                }
                // 判断bean是否已经存在
                if (checkCandidate(name, def)) {
                    // 注册bean definition(只是注册到registry容器中,并非是beanFactory)
                    BeanDefinitionHolder holder = new BeanDefinitionHolder(def, name);
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, this.registry);
                    // 生成bean信息
                    beanDatas.add(createBeanData(srcName, def, name, modify, def.getScope()));
                    // 处理@Configuration+@Bean注解的类
                    handleConfigurationBean(envId, plugin, beanDatas);
                }
            }
            pd.setBeans(beanDatas.toArray(new BeanData[0]));
        }
        return pds.toArray(new PluginData[0]);
    }


    @Override
    public void addExcludeFilter(TypeFilter excludeFilter) {
        super.addExcludeFilter(excludeFilter);
    }


    /**
     * 处理@Configuration+@Bean注解的类
     * @param envId 环境id
     * @param infos bean信息集合
     */
    private void handleConfigurationBean(String envId, IPlugin plugin, List<BeanData> beans) {
        ConfigurationClassPostProcessor postProcessor = new ConfigurationClassPostProcessor();
        int beforeCount = registry.getBeanDefinitionCount();
        postProcessor.postProcessBeanDefinitionRegistry(registry);
        int afterCount = registry.getBeanDefinitionCount();
        if (afterCount > beforeCount) {
            String[] names = registry.getBeanDefinitionNames();
            for (int j = afterCount - beforeCount; j > 0; j--) {
                // 获取bean原名
                String srcName = names[names.length - j];
                AnnotatedBeanDefinition def = (AnnotatedBeanDefinition) registry.getBeanDefinition(srcName);
                // 修改bean名称
                String name = PluginManagerFactory.beanNameModifier.modify(envId, srcName);
                // 获取@Bean方法上的注解
                MethodMetadata metadata = def.getFactoryMethodMetadata();
                MergedAnnotations annotations = metadata.getAnnotations();
                // 包含@NoModify注解
                boolean modify = true;
                if (annotations.isPresent(NoModify.class)) {
                    // bean名称不进行修改
                    name = srcName;
                    modify = false;
                }

                String scopeStr = ConfigurableBeanFactory.SCOPE_SINGLETON;
                if (annotations.isPresent(Scope.class)) {
                    // 获取scope注解
                    MergedAnnotation<Scope> scope = annotations.get(Scope.class);
                    scopeStr = scope.getString("value");
                }
                // 校验候选bean
                if (checkCandidate(name, def)) {
                    registry.removeBeanDefinition(srcName);
                    registry.registerBeanDefinition(name, def);
                    beans.add(createBeanData(srcName, def, name, modify, scopeStr));
                } else {
                    if (srcName.endsWith(name)) {
                        beans.add(createBeanData(srcName, def, name, modify, scopeStr));
                    }
                }
            }
        }
    }

    private PluginData createPluginData(IPlugin plugin) {
        PluginData pd = new PluginData();
        pd.setEnvId(plugin.envId());
        pd.setEnvName(plugin.envName());
        pd.setName(plugin.name());
        pd.setSize(plugin.size());
        return pd;
    }

    private BeanData createBeanData(String srcName, BeanDefinition def, String name, boolean modify, String scope) {
        BeanData bi = new BeanData();
        // 这里设置原始名字
        bi.setName(name);
        bi.setModifyName(modify);
        bi.setSrcName(srcName);
        String className = def.getBeanClassName();
        if (StringHelper.isNull(className)) {
            Object source = def.getSource();
            if (source instanceof MethodMetadata) {
                MethodMetadata meta = (MethodMetadata) source;
                className = meta.getReturnTypeName();
            } else {
                className = def.getFactoryBeanName();
            }
        }
        bi.setClassname(className);
        bi.setScope(scope);
        return bi;
    }

    private List<AnnotatedBeanDefinition> findCandidate(URL jarURL) {
        // List<BeanDefinition> candidates = new ArrayList<>();
        List<AnnotatedBeanDefinition> candidates = new ArrayList<>();

        // 过滤出所有class文件
        Resource[] resources = findResource(jarURL);
        boolean traceEnable = LOGGER.isTraceEnabled();

        for (Resource resource : resources) {
            if (traceEnable) {
                LOGGER.trace("Scanning " + resource);
            }
            try {
                if (resource.isReadable()) {
                    // MetadataReader reader = new SimpleMetadataReaderFactory(this.registry).getMetadataReader(resource);
                    MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadata cm = reader.getClassMetadata();
                    String className = cm.getClassName();
                    if (isInclude(className) && isCandidateComponent(reader)) {
                        // 生成bean定义
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(reader);
                        sbd.setResource(resource);
                        sbd.setSource(resource);

                        if (isCandidateComponent(sbd)) {
                            candidates.add(sbd);
                        }
                    }
                }
            } catch (Throwable ex) {
                throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
            }
        }

        return candidates;
    }

    private boolean isInclude(String className) {
        for (String excludeClassName : excludeClassNames) {
            if (className.startsWith(excludeClassName)) {
                return false;
            }
        }
        String packageName = ClassUtils.getPackageName(className);
        for (String pkg : packages) {
            if (packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过将给定的path，转换成Resource
     * @param path 路径
     * @return Resource[]
     */
    private Resource[] findResource(URL url) {
        ResourceResolver resolver = new ResourceResolver();
        Set<Resource> result = null;

        if (url != null) {
            Resource resource = new UrlResource(url);
            try {
                // 过滤出所有class文件
                result = resolver.doFindPathMatchingJarResources(resource, url, pattern);
            } catch (IOException e) {
                LOGGER.error("can't find {} resource", url.getPath());
            }
        }

        return result == null ? new Resource[]{} : result.toArray(new Resource[0]);

    }

    protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
        beanDefinition.applyDefaults(this.beanDefDefaults);
        if (this.autowireCandidatePatterns != null) {
            beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(this.autowireCandidatePatterns, beanName));
        }
    }

    /**
     * 检验bean定义是否有效
     * @param beanName
     * @param def
     * @return
     * @throws IllegalStateException
     */
    protected boolean checkCandidate(String beanName, BeanDefinition def) throws IllegalStateException {
        // 判断bean是否已经存在
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        // 判断是否兼容.如果兼容,不进行注册
        if (isCompatible(def, existingDef)) {
            return false;
        }
        // bean定义重复,抛错
        throw new BeanDefinitionStoreException(
            "Annotation-specified bean name '" + beanName + "' for bean class [" + def.getBeanClassName()
                + "] conflicts with existing, " + "non-compatible bean definition of same name and class [" + existingDef
                .getBeanClassName() + "]");
    }

    protected boolean isCompatible(BeanDefinition newDef, BeanDefinition existingDef) {
        if (!(existingDef instanceof ScannedGenericBeanDefinition)) {
            return true;
        }
        if (newDef.getSource().equals(existingDef.getSource())) {
            return true;
        }
        if (newDef.equals(existingDef)) {
            return true;
        }
        return false;
        // return (!(existingDef instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
        //     newDef.getSource().equals(existingDef.getSource()) ||  // scanned same file twice
        //     newDef.equals(existingDef));  // scanned equivalent class twice
    }

    private static class ResourceResolver extends PathMatchingResourcePatternResolver {

        public ResourceResolver() {
            super();
        }

        @Override
        protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, URL rootDirURL, String subPattern)
            throws IOException {
            return super.doFindPathMatchingJarResources(rootDirResource, rootDirURL, subPattern);
        }

        @Override
        protected void addAllClassLoaderJarRoots(ClassLoader classLoader, Set<Resource> result) {
            super.addAllClassLoaderJarRoots(classLoader, result);
        }

    }
}
