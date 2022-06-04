package com.x.plugin.facotry.config;

import com.x.plugin.core.IPlugin;
import com.x.plugin.core.config.IConfigGetter;
import com.x.plugin.core.filter.RegexMatchFilter;
import com.x.plugin.data.Config;
import com.x.plugin.enums.ConfigType;
import com.x.plugin.enums.ProfileType;
import com.x.plugin.facotry.filter.ApplicationPropertiesMatchFilter;
import com.x.plugin.facotry.filter.ApplicationPropertiesMainMatchFilter;
import com.x.plugin.facotry.filter.ApplicationYmlMatchFilter;
import com.x.plugin.facotry.filter.ApplicationYmlMainMatchFilter;
import com.x.plugin.facotry.filter.BootstrapPropertiesMatchFilter;
import com.x.plugin.facotry.filter.BootstrapYmlMatchFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <li>file:./config/</li>
 * <li>file:./</li>
 * <li>classpath:config/</li>
 * <li>classpath:</li>
 * @author AD
 * @date 2022/5/3 10:09
 */
public class FileSystemConfigGetter implements IConfigGetter {

    private static final RegexMatchFilter bootPropertiesFilter = new BootstrapPropertiesMatchFilter();
    private static final RegexMatchFilter bootYmlFilter = new BootstrapYmlMatchFilter();
    private static final RegexMatchFilter appPropertiesFilter = new ApplicationPropertiesMatchFilter();
    private static final RegexMatchFilter appPropertiesMainFilter = new ApplicationPropertiesMainMatchFilter();
    private static final RegexMatchFilter appYmlFilter = new ApplicationYmlMatchFilter();
    private static final RegexMatchFilter appYmlMainFilter = new ApplicationYmlMainMatchFilter();
    private final File jarDir;
    private final Map<String, Config> configs;

    public FileSystemConfigGetter(IPlugin plugin) throws Exception {
        File file = new File(plugin.url().toURI());
        Objects.requireNonNull(file, "插件所在文件目录不能为空");
        this.jarDir = file.getParentFile();
        this.configs = getConfigFiles();
    }

    public FileSystemConfigGetter(File jarDir) throws IOException {
        Objects.requireNonNull(jarDir, "插件所在文件目录不能为空");
        this.jarDir = jarDir;
        this.configs = getConfigFiles();
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
    private Map<String, Config> getConfigFiles() throws IOException {
        // 1.首先获取config目录下的文件
        File[] configFolders = jarDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && file.getName().equalsIgnoreCase("config");
            }
        });
        File dir = null;
        Map<String, Config> configs = null;
        if (configFolders != null && configFolders.length > 0) {
            // 获取/config目录下的配置文件
            dir = configFolders[0];
            configs = queryConfigs(dir);
        }
        if (configs == null || configs.size() < 1) {
            // 获取根目录(/)下的配置文件
            dir = jarDir;
            configs = queryConfigs(dir);
        }
        return configs;
    }

    private Map<String, Config> queryConfigs(File dir) throws IOException {
        Map<String, Config> propFiles = new LinkedHashMap<>();
        Map<String, Config> ymlFiles = new LinkedHashMap<>();
        // 先遍历properties的配置文件
        fillConfigs(bootPropertiesFilter, appPropertiesFilter, dir, ConfigType.properties, propFiles, appPropertiesMainFilter);
        // 存在properties的配置文件,返回
        if (propFiles.size() > 0) {
            return propFiles;
        } else {
            // 如果不存在properties的配置文件,再查找yml或yaml的文件
            fillConfigs(bootYmlFilter, appYmlFilter, dir, ConfigType.yml, ymlFiles, appYmlMainFilter);
            return ymlFiles;
        }
    }

    private void fillConfigs(RegexMatchFilter bootFilter, RegexMatchFilter appFilter, File dir, ConfigType type, Map<String, Config> container, RegexMatchFilter mainConfigFilter)
        throws IOException {
        File[] configFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // 过滤出bootstrap和application的配置文件
                return file.isFile() && (bootPropertiesFilter.accept(file.getName()) || bootYmlFilter.accept(file.getName())
                    || appPropertiesFilter.accept(file.getName()) || appYmlFilter.accept(file.getName()));
            }
        });
        for (File configFile : configFiles) {
            // 先查找bootstrap的配置文件
            if (bootFilter.accept(configFile.getName())) {
                Config bootConf = generateConfig(configFile, type, true, false, ProfileType.BOOT);
                container.put(bootConf.getProfile(), bootConf);
            } else {
                // 查询application配置文件
                if (appFilter.accept(configFile.getName())) {
                    // 查找主要配置文件
                    if (mainConfigFilter.accept(configFile.getName())) {
                        Config mainConf = generateConfig(configFile, type, false, true, ProfileType.MAIN);
                        container.put(mainConf.getProfile(), mainConf);
                    } else {
                        String profile = configFile.getName().split("\\.")[0].split("-")[1];
                        Config otherConf = generateConfig(configFile, type, false, false, profile);
                        container.put(otherConf.getProfile(), otherConf);
                    }
                }
            }
        }
    }

    private Config generateConfig(File file, ConfigType type, boolean isBoot, boolean isMain, String profile) throws IOException {
        Config conf = new Config();
        conf.setName(file.getName());
        conf.setType(type);
        conf.setUrl(file.toURI().toURL());
        conf.setBoot(isBoot);
        conf.setMain(isMain);
        conf.setProfile(profile);
        conf.setContext(getConfigContext(conf));
        return conf;
    }

}
