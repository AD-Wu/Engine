package com.x.plugin.facotry.config;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.config.IConfigGetter;
import com.x.plugin.core.filter.RegexMatchFilter;
import com.x.plugin.data.Config;
import com.x.plugin.enums.ConfigType;
import com.x.plugin.enums.ProfileType;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author AD
 * @date 2022/5/4 11:33
 */
public class ClasspathConfigGetter implements IConfigGetter {


    private final URL jarUrl;
    private final Map<String, Config> configs;

    public ClasspathConfigGetter(IPlugin plugin) throws IOException {
        this.jarUrl = plugin.url();
        Objects.requireNonNull(jarUrl, "插件URL不能为空");
        this.configs = analiseConfigs();
    }

    @Override
    public Map<String, Config> getConfigs() {
        return configs;
    }


    /**
     * 获取配置文件
     * @return (k, v)=(profile,Config)
     * @throws MalformedURLException
     */
    private Map<String, Config> analiseConfigs() throws IOException {
        Map<String, Config> configProps = new LinkedHashMap<>();
        Map<String, Config> configYmls = new LinkedHashMap<>();
        Map<String, Config> props = new LinkedHashMap<>();
        Map<String, Config> ymls = new LinkedHashMap<>();
        JarURLConnection conn = (JarURLConnection) jarUrl.openConnection();
        if (conn != null) {
            // 获取jar(即jarAbsPath所对应的jar包对象)
            try (JarFile jarFile = conn.getJarFile()) {
                if (jarFile != null) {
                    // 获取jar包的内部结构(文件|文件夹都是JarEntry,文件也包括jar包)
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry entry = jarEntries.nextElement();
                        String name = entry.getName();
                        // 查找/config/目录下的application.properties或bootstrap.properties文件
                        if (configAppPropertiesFilter.accept(name) || configBootPropertiesFilter.accept(name)) {
                            Config conf = generateConfig(name,
                                                         ConfigType.properties,
                                                         configAppPropertiesMainFilter,
                                                         configBootPropertiesFilter);
                            configProps.put(conf.getProfile(), conf);
                        }

                        // 查找/config/目录下的application.yml或bootstrap.yml文件
                        else if (configAppYmlFilter.accept(name) || configBootYmlFilter.accept(name)) {
                            Config conf = generateConfig(name, ConfigType.yml, configAppYmlMainFilter, configBootYmlFilter);
                            configYmls.put(conf.getProfile(), conf);
                        }
                        // 查找根目录下的application.properties或bootstrap.properties文件
                        else if (appPropertiesFilter.accept(name) || bootPropertiesFilter.accept(name)) {
                            Config conf = generateConfig(name,
                                                         ConfigType.properties,
                                                         appPropertiesMainFilter,
                                                         bootPropertiesFilter);
                            props.put(conf.getProfile(), conf);
                        }
                        // 查找根目录下的application.yml或bootstrap.yml文件
                        else if (appYmlFilter.accept(name) || bootYmlFilter.accept(name)) {
                            Config conf = generateConfig(name, ConfigType.yml, appYmlMainFilter, bootYmlFilter);
                            ymls.put(conf.getProfile(), conf);
                        }
                    }
                }
            }
            // 优先级:properties > yml,config目录 > 根目录
            if (configProps.size() > 0) {
                return configProps;
            } else if (configYmls.size() > 0) {
                return configYmls;
            } else if (props.size() > 0) {
                return props;
            } else if (ymls.size() > 0) {
                return ymls;
            }
        }
        return new LinkedHashMap<>();
    }

    private Config generateConfig(String name, ConfigType type, RegexMatchFilter mainConfigFilter, RegexMatchFilter bootConfigFilter)
        throws IOException {
        Config conf = new Config();
        conf.setName(name);
        conf.setType(type);
        conf.setUrl(new URL(jarUrl.toString() + name));
        conf.setContext(getConfigContext(conf));
        if (bootConfigFilter.accept(name)) {
            conf.setBoot(true);
            conf.setProfile(ProfileType.BOOT);
        } else if (mainConfigFilter.accept(name)) {
            conf.setMain(true);
            conf.setProfile(ProfileType.MAIN);
        } else {
            conf.setMain(false);
            String profile = name.split("\\.")[0].split("-")[1];
            conf.setProfile(profile);
        }
        return conf;
    }

    private static final RegexMatchFilter configBootPropertiesFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/bootstrap.properties$"};
        }
    };

    private static final RegexMatchFilter configBootYmlFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/bootstrap.y[a]?ml$"};
        }
    };

    private static final RegexMatchFilter configAppPropertiesFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/application[-]?[a-z]*.properties$"};
        }
    };

    private static final RegexMatchFilter configAppYmlFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/application[-]?[a-z]*.y[a]?ml$"};
        }
    };

    private static final RegexMatchFilter configAppPropertiesMainFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/application.y[a]?ml$"};
        }
    };

    private static final RegexMatchFilter configAppYmlMainFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^config/application.properties$"};
        }
    };


    private static final RegexMatchFilter bootPropertiesFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^bootstrap.properties$"};
        }
    };

    private static final RegexMatchFilter bootYmlFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^bootstrap.y[a]?ml$"};
        }
    };

    private static final RegexMatchFilter appPropertiesFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^application[-]?[a-z]*.properties$"};
        }
    };
    private static final RegexMatchFilter appYmlFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^application[-]?[a-z]*.y[a]?ml$"};
        }
    };
    private static final RegexMatchFilter appPropertiesMainFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^application.properties$"};
        }
    };
    private static final RegexMatchFilter appYmlMainFilter = new RegexMatchFilter() {
        @Override
        protected String[] getPatterns() {
            return new String[]{"^application.y[a]?ml$"};
        }
    };


}
