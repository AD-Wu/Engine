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
                        // ??????????????????
                        conf = gen.getPluginConfig();
                        // ????????????????????????????????????????????????
                        Class<?> mainClass = app.getMainApplicationClass();
                        StandardAnnotationMetadata sam = new StandardAnnotationMetadata(mainClass);
                        PackageHelper pi = new PackageHelper(sam);
                        packages = pi.getPackageNames();
                        excludeClassNames = pi.getExcludeClassNames();
                        // ????????????
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
            logger.info("????????????????????????,???????????????");
            return;
        }
        // ----------------------------- ???????????? -----------------------------
        try {
            autoScanPlugins();
        } catch (Exception e) {
            return;
        }
        // ----------------------------- ????????????????????? -----------------------------
        if (pluginMap.isEmpty()) {
            try {
                manualScanPlugins();
            } catch (Exception e) {
                return;
            }
        }
        // ----------------------------- ?????????JVM -----------------------------
        // ???????????????JVM
        IPlugin[] plugins = pluginMap.values().toArray(new IPlugin[0]);
        PluginRegistry pluginRegistry = new PluginRegistry(plugins);
        try {
            pluginRegistry.registerToJVM();
        } catch (Exception e) {
            logger.error("??????????????????:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        // ??????bean
        PluginData[] datas = null;
        try {
            datas = scanBean(plugins);
        } catch (Exception e) {
            logger.error("??????Bean??????:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        // ----------------------------- ??????????????????????????? -----------------------------
        // ?????????????????????????????????
        initPluginManagerFactory(datas);
        logs.clear();
        pluginMap.clear();
        conf = null;
    }

    protected abstract boolean isConfigFile(String configFileName);

    protected abstract IPluginConfigGenerator getPluginConfigGenerator(Map<String, Object> config);

    protected abstract List<IPlugin> getPlugins(String... dirs) throws Exception;

    private PluginData[] scanBean(IPlugin[] plugins) throws Exception {
        // ??????Bean????????????
        BeanDefinitionRegistry ctx = new GenericApplicationContext();
        // ??????Bean?????????
        BeanScanner scanner = new BeanScanner(ctx, plugins, packages, excludeClassNames);
        // ??????????????????bean??????
        PluginData[] datas = scanner.scan();
        for (String name : ctx.getBeanDefinitionNames()) {
            // ???context?????????bean??????
            BeanDefinition def = ctx.getBeanDefinition(name);
            // ???bean???????????????bean??????
            PluginManagerFactory.putBeanDefinition(name, def);
        }
        return datas;
    }

    private void autoScanPlugins() throws Exception {
        CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
        URL url = src.getLocation();
        IPluginLoader loader = null;
        if (UrlProtocol.jar.toString().equalsIgnoreCase(url.getProtocol())) {
            logger.info("??????????????????:???Jar???");
            loader = new JarPluginLoader();
        } else {
            if (UrlProtocol.file.toString().equalsIgnoreCase(url.getProtocol())) {
                logger.info("??????????????????:???File???");
                File file = new File(url.getFile() + PluginProperties.pluginsPath);
                loader = new FilePluginLoader(file);
            } else {
                logger.error("??????????????????:???{}???", url.getProtocol());
                return;
            }
        }
        try {
            IPlugin[] ps = loader.getPlugins();
            saveToMap(ps);
        } catch (Exception e) {
            logger.error("????????????????????????:{}", StringHelper.getExceptionTrace(e));
            return;
        }
        if (pluginMap.isEmpty()) {
            logger.info("???????????????????????????0??????,??????????????????????????????");
        }
        logger.info(splitLine);
    }

    private void manualScanPlugins() throws Exception {
        // ????????????
        try {
            saveToMap(getCommonPlugins());
        } catch (Exception e) {
            logger.error("????????????????????????:{}", StringHelper.getExceptionTrace(e));
            throw e;
        }
        // ????????????
        try {
            saveToMap(getBusinessPlugins());
        } catch (Exception e) {
            logger.error("????????????????????????:{}", StringHelper.getExceptionTrace(e));
            throw e;
        }
    }

    private IPlugin[] getBusinessPlugins() throws Exception {
        List<IPlugin> plugins = getPlugins(conf.getBusinessDirs().toArray(new String[0]));
        // ????????????
        logger.info("??????????????????{}??????", plugins.size());
        for (int i = 0, c = plugins.size(); i < c; i++) {
            IPlugin p = plugins.get(i);
            logger.info("\t??????{}??????????????????:???{}???", i + 1, p.name());
        }
        return plugins.toArray(new IPlugin[0]);
    }

    private IPlugin[] getCommonPlugins() throws Exception {
        List<IPlugin> plugins = getPlugins(conf.getCommonDirs().toArray(new String[0]));
        // ????????????
        logger.info("??????????????????{}??????", plugins.size());
        for (int i = 0, c = plugins.size(); i < c; i++) {
            IPlugin p = plugins.get(i);
            logger.info("\t??????{}??????????????????:???{}???", i + 1, p.name());
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
                    logger.error("???{}????????????{}????????????????????????{}????????????", p.envName(), p.name(), old.envName());
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
                    // ????????????
                    int beanCount = p.getBeans().length;
                    logger.info("???{}???????????????{}???????????????{}?????????{}??????Bean", p.getEnvName(), i + 1, p.getName(), beanCount);
                    if (beanCount > 0) {
                        BeanData[] bds = p.getBeans();
                        for (int j = 0; j < bds.length; j++) {
                            BeanData bi = bds[j];
                            logger.info("\t???{}???-???{}???-???{}???", j + 1, bi.getName(), bi.getClassname());
                        }
                    }
                }
                PluginManagerFactory.putPluginManager(manager);
            });
            logger.info(splitLine);
        } else {
            logger.error("??????????????????,??????????????????,?????????????????????????????????");
            logger.info(splitLine);
        }
    }

    private void logConfig(String name) {
        addLog(LogLevel.INFO, "??????????????????{}??????????????????????????????:", name);
        String[] comms = conf.getCommonDirs().toArray(new String[0]);
        addLog(LogLevel.INFO, "???????????????????????????{}??????", comms.length);
        for (int i = 0, c = comms.length; i < c; i++) {
            addLog(LogLevel.INFO, "\t???{}???-???{}???", i + 1, comms[i]);
        }
        String[] busis = conf.getBusinessDirs().toArray(new String[0]);
        addLog(LogLevel.INFO, "???????????????????????????{}??????", busis.length);
        for (int i = 0, c = busis.length; i < c; i++) {
            addLog(LogLevel.INFO, "\t???{}???-???{}???", i + 1, busis[i]);
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
