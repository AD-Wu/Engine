package com.x.plugin.facotry;

import com.x.plugin.core.IPluginConfigGenerator;
import com.x.plugin.core.filter.RegexFindFilter;
import com.x.plugin.data.PluginConfig;
import com.x.plugin.util.StringHelper;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/5/13 14:13
 */
public class PluginConfigGenerator implements IPluginConfigGenerator {

    private static final RegexFindFilter appYml = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "application.yml";
        }
    };

    private static final RegexFindFilter appYaml = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "application.yaml";
        }
    };

    private static final RegexFindFilter appProp = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "application.properties";
        }
    };

    private static final RegexFindFilter bootYml = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "bootstrap.yml";
        }
    };

    private static final RegexFindFilter bootYaml = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "bootstrap.yaml";
        }
    };

    private static final RegexFindFilter bootProp = new RegexFindFilter() {
        @Override
        protected String getPattern() {
            return "bootstrap.properties";
        }
    };

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, Object> config;

    public PluginConfigGenerator(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public boolean isValid() {
        if (config != null && config.size() > 0) {
            return checkBusinessDirs();
        }
        return false;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return createConfig();
    }

    @Override
    public String fixName(String name) {
        if (appYml.accept(name)) {
            return "application.yml";
        } else if (appYaml.accept(name)) {
            return "application.yaml";
        } else if (appProp.accept(name)) {
            return "application.properties";
        } else if (bootYml.accept(name)) {
            return "bootstrap.yml";
        } else if (bootYaml.accept(name)) {
            return "bootstrap.yaml";
        } else if (bootProp.accept(name)) {
            return "bootstrap.properties";
        }
        return name;
    }

    private boolean checkMode() {
        if (checkConfig(PluginConfig.Key.mode)) {
            return true;
        }
        logger.error("请配置插件运行模式:plugin.mode=abs或src");
        return false;
    }

    private boolean checkBusinessDirs() {
        if (checkConfig(PluginConfig.Key.business)) {
            return true;
        } else {
            if (checkConfig(PluginConfig.Key.getBusinessKey(0))) {
                return true;
            }
            logger.error("请配置业务插件目录:plugin.business=xxx.jar 或 jar所在文件夹");
            return false;
        }
    }

    private boolean checkConfig(String key) {
        Object value = config.get(key);
        if (value == null) {
            logger.info("未配置:{}", key);
            return false;
        }
        return true;
    }

    private PluginConfig createConfig() {
        PluginConfig conf = new PluginConfig();
        conf.setCommonDirs(getDirs(PluginConfig.Key.commons));
        conf.setBusinessDirs(getDirs(PluginConfig.Key.business));
        return conf;
    }

    private Set<String> getDirs(String key) {
        Set<String> dirs = new LinkedHashSet<>();
        Object value = config.get(key);
        if (value != null) {
            String[] vs = value.toString().replace(",", ";").split(";");
            for (String dir : vs) {
                if(StringHelper.isNotNull(dir)){
                    dirs.add(dir);
                }
            }
        } else {
            int i = 0;
            while ((value = config.get(PluginConfig.Key.getIndexKey(key, i))) != null && i < Short.MAX_VALUE) {
                String dir = StringHelper.toString(value);
                if (StringHelper.isNotNull(dir)) {
                    dirs.add(dir);
                }
                i++;
            }
        }
        return dirs;
    }
}
