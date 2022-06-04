package com.x.plugin.spring;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.IPluginConfigGenerator;
import com.x.plugin.core.IPluginLoader;
import com.x.plugin.data.BeanData;
import com.x.plugin.data.PluginConfig;
import com.x.plugin.data.PluginData;
import com.x.plugin.enums.PluginProperties;
import com.x.plugin.enums.UrlProtocol;
import com.x.plugin.facotry.FilePluginLoader;
import com.x.plugin.facotry.JarPluginLoader;
import com.x.plugin.util.ConfigFileHelper;
import com.x.plugin.util.StringHelper;
import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

/**
 * @author AD
 * @date 2022/4/26 12:02
 */
public abstract class BasePluginIniter implements ImportBeanDefinitionRegistrar, EnvironmentPostProcessor {

    public static final String splitLine = "--------------------------------------------------------------";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Map<String, LogLevel> logs = new LinkedHashMap<>();

    private static Map<String, IPlugin> pluginMap = new LinkedHashMap<>();

    private static PluginConfig conf;

    private static Set<String> packages;
    private static Set<String> excludeClassNames;


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        if (conf == null) {
            MutablePropertySources mps = env.getPropertySources();
            Iterator<PropertySource<?>> it = mps.iterator();
            while (it.hasNext()) {
                PropertySource<?> ps = it.next();
                String name = ps.getName();
                if (isConfigFile(name)) {
                    Map<String, Object> ctx = ConfigFileHelper.parseConfig(ps);
                    IPluginConfigGenerator gen = getPluginConfigGenerator(ctx);
                    if (gen != null && gen.isValid()) {
                        // 获取插件配置
                        conf = gen.getPluginConfig();
                        // 获取扫描包路径集合和排除的类集合
                        Class<?> mainClass = app.getMainApplicationClass();
                        StandardAnnotationMetadata sam = new StandardAnnotationMetadata(mainClass);
                        PackageHelper pi = new PackageHelper(sam);
                        packages = pi.getPackageNames();
                        excludeClassNames = pi.getExcludeClassNames();
                        // 日志记录
                        logConfig(gen.fixName(name));
                    }
                }
            }

        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata am, BeanDefinitionRegistry registry) {
        printLog();
        if (conf == null) {
            logger.info("未获取到插件配置,不注册插件");
            return;
        }
        // ----------------------------- 自动扫描 -----------------------------
        try {
            autoScanPlugins();
        } catch (Exception e) {
            return;
        }
        // ----------------------------- 扫描配置的路径 -----------------------------
        if (pluginMap.isEmpty()) {
            try {
                manualScanPlugins();
            } catch (Exception e) {
                return;
            }
        }
        // ----------------------------- 注册到JVM -----------------------------
        // 注册插件到JVM
        IPlugin[] plugins = pluginMap.values().toArray(new IPlugin[0]);
        PluginRegistry pluginRegistry = new PluginRegistry(plugins);
        try {
            pluginRegistry.registerToJVM();
        } catch (Exception e) {
            logger.error("注册插件失败:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        // 扫描bean
        PluginData[] datas = null;
        try {
            datas = scanBean(plugins);
        } catch (Exception e) {
            logger.error("扫描Bean异常:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        // ----------------------------- 初始化插件管理工厂 -----------------------------
        // 将插件信息存入到工厂中
        initPluginManagerFactory(datas);
        logs.clear();
        pluginMap.clear();
        conf = null;
    }

    protected abstract boolean isConfigFile(String configFileName);

    protected abstract IPluginConfigGenerator getPluginConfigGenerator(Map<String, Object> config);

    protected abstract List<IPlugin> getPlugins(String... dirs) throws Exception;

    private PluginData[] scanBean(IPlugin[] plugins) throws Exception {
        // 定义Bean定义容器
        BeanDefinitionRegistry ctx = new GenericApplicationContext();
        // 创建Bean扫描器
        BeanScanner scanner = new BeanScanner(ctx, plugins, packages, excludeClassNames);
        // 扫描插件获得bean信息
        PluginData[] datas = scanner.scan();
        for (String name : ctx.getBeanDefinitionNames()) {
            // 从context中获取bean定义
            BeanDefinition def = ctx.getBeanDefinition(name);
            // 将bean信息保存到bean工厂
            PluginManagerFactory.putBeanDefinition(name, def);
        }
        return datas;
    }

    private void autoScanPlugins() throws Exception {
        CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
        URL url = src.getLocation();
        IPluginLoader loader = null;
        if (UrlProtocol.jar.toString().equalsIgnoreCase(url.getProtocol())) {
            logger.info("插件扫描模式:【Jar】");
            loader = new JarPluginLoader();
        } else {
            if (UrlProtocol.file.toString().equalsIgnoreCase(url.getProtocol())) {
                logger.info("插件扫描模式:【File】");
                File file = new File(url.getFile() + PluginProperties.pluginsPath);
                loader = new FilePluginLoader(file);
            } else {
                logger.error("不支持该协议:【{}】", url.getProtocol());
                return;
            }
        }
        try {
            IPlugin[] ps = loader.getPlugins();
            saveToMap(ps);
        } catch (Exception e) {
            logger.error("自动扫描插件异常:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        if (pluginMap.isEmpty()) {
            logger.info("自动扫描找到插件【0】个,开始扫描所配置的路径");
        }
        logger.info(splitLine);
    }

    private void manualScanPlugins() throws Exception {
        // 公共插件
        try {
            saveToMap(getCommonPlugins());
        } catch (Exception e) {
            logger.error("获取公共插件异常:{}", StringHelper.getExceptionTrace(e));
            throw e;
        }
        // 业务插件
        try {
            saveToMap(getBusinessPlugins());
        } catch (Exception e) {
            logger.error("获取业务插件异常:{}", StringHelper.getExceptionTrace(e));
            throw e;
        }
    }

    private IPlugin[] getBusinessPlugins() throws Exception {
        List<IPlugin> plugins = getPlugins(conf.getBusinessDirs().toArray(new String[0]));
        // 日志记录
        logger.info("业务插件共【{}】个", plugins.size());
        for (int i = 0, c = plugins.size(); i < c; i++) {
            IPlugin p = plugins.get(i);
            logger.info("\t第【{}】个业务插件:【{}】", i + 1, p.name());
        }
        return plugins.toArray(new IPlugin[0]);
    }

    private IPlugin[] getCommonPlugins() throws Exception {
        List<IPlugin> plugins = getPlugins(conf.getCommonDirs().toArray(new String[0]));
        // 日志记录
        logger.info("公共插件共【{}】个", plugins.size());
        for (int i = 0, c = plugins.size(); i < c; i++) {
            IPlugin p = plugins.get(i);
            logger.info("\t第【{}】个公共插件:【{}】", i + 1, p.name());
        }
        return plugins.toArray(new IPlugin[0]);
    }

    private void saveToMap(IPlugin[] ps) {
        if (ps != null && ps.length > 0) {
            for (IPlugin p : ps) {
                if (!pluginMap.containsKey(p.name())) {
                    pluginMap.put(p.name(), p);
                } else {
                    IPlugin old = pluginMap.get(p.name());
                    logger.error("【{}】环境【{}】插件已存在于【{}】环境中", p.envName(), p.name(), old.envName());
                }
            }
        }
    }

    private void initPluginManagerFactory(PluginData[] datas) {
        if (datas != null && datas.length > 0) {
            Map<String, List<PluginData>> collects = Arrays.stream(datas)
                .collect(Collectors.groupingBy(p -> p.getEnvId(), LinkedHashMap::new, Collectors.toList()));
            collects.forEach((envId, pds) -> {
                PluginManager manager = new PluginManager(envId);
                for (int i = 0, c = pds.size(); i < c; i++) {
                    PluginData p = pds.get(i);
                    manager.addPluginData(p);
                    // 日志记录
                    int beanCount = p.getBeans().length;
                    logger.info("【{}】环境第【{}】个插件【{}】共【{}】个Bean", p.getEnvName(), i + 1, p.getName(), beanCount);
                    if (beanCount > 0) {
                        BeanData[] bds = p.getBeans();
                        for (int j = 0; j < bds.length; j++) {
                            BeanData bi = bds[j];
                            logger.info("\t【{}】-【{}】-【{}】", j + 1, bi.getName(), bi.getClassname());
                        }
                    }
                }
                PluginManagerFactory.putPluginManager(manager);
            });
            logger.info(splitLine);
        } else {
            logger.error("没有插件信息,无法自动注入,将可能导致程序无法运行");
            logger.info(splitLine);
        }
    }

    private void logConfig(String name) {
        addLog(LogLevel.INFO, "从配置文件【{}】中取得插件配置如下:", name);
        String[] comms = conf.getCommonDirs().toArray(new String[0]);
        addLog(LogLevel.INFO, "公共插件目录配置【{}】个", comms.length);
        for (int i = 0, c = comms.length; i < c; i++) {
            addLog(LogLevel.INFO, "\t【{}】-【{}】", i + 1, comms[i]);
        }
        String[] busis = conf.getBusinessDirs().toArray(new String[0]);
        addLog(LogLevel.INFO, "业务插件目录配置【{}】个", busis.length);
        for (int i = 0, c = busis.length; i < c; i++) {
            addLog(LogLevel.INFO, "\t【{}】-【{}】", i + 1, busis[i]);
        }
        addLog(LogLevel.INFO, splitLine);
    }


    private void addLog(LogLevel logLevel, String pattern, Object... params) {
        String replace = pattern.replace("{}", "%s");
        Formatter formatter = new Formatter();
        Formatter format = formatter.format(replace, params);
        logs.put(format.toString(), logLevel);
    }

    private void printLog() {
        logs.forEach((txt, level) -> {
            switch (level) {
                case TRACE:
                    logger.trace(txt);
                    break;
                case DEBUG:
                    logger.debug(txt);
                    break;
                case INFO:
                    logger.info(txt);
                    break;
                case WARN:
                    logger.warn(txt);
                    break;
                case ERROR:
                    logger.error(txt);
                    break;
                case FATAL:
                case OFF:
                default:
                    break;
            }
        });
    }

}
