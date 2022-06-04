package com.x.isearch.server.core;

import com.x.doraemon.Strings;
import com.x.isearch.server.data.config.DataMoveConfig;
import com.x.isearch.server.data.config.converter.ConverterConfig;
import com.x.isearch.server.data.config.source.DataSourceConfig;
import com.x.isearch.server.data.config.source.EsSourceConfig;
import com.x.isearch.server.es.ESClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 * @author AD
 * @date 2022/3/28 13:20
 */
public class Context {

    // -------------------------------- 常量 --------------------------------

    private static final Map<DataSourceConfig, String> dataSourceConfigs = new HashMap<>();
    private static final Map<String, DataSource> dataSources = new HashMap<>();

    private static final Map<EsSourceConfig, String> esSourceConfigs = new HashMap<>();
    private static final Map<String, ESClient> esSources = new HashMap<>();

    // -------------------------------- 成员变量 --------------------------------

    private final ConverterConfig converterConfig;

    // -------------------------------- 静态方法 --------------------------------

    public static synchronized void initSource(DataMoveConfig conf) throws Exception {
        initDataSource(conf.getDataSourceConfigs());
        initEsSource(conf.getEsSourceConfigs());
    }

    // -------------------------------- 构造方法 --------------------------------

    /**
     * 构造方法
     * @param config 配置参数
     */
    public Context(ConverterConfig conf) {
        this.converterConfig = conf;
    }

    // -------------------------------- 成员方法 --------------------------------

    public static DataSource getDataSource(String name) {
        return dataSources.get(name);
    }

    public static ESClient getESClient(String name) {
        return esSources.get(name);
    }

    public ConverterConfig getConverterConfig() {
        return converterConfig;
    }

    // -------------------------------- 私有方法 --------------------------------

    private static void initDataSource(DataSourceConfig[] dcs) throws Exception {
        if (dcs != null && dcs.length > 0) {
            for (int i = 0; i < dcs.length; i++) {
                DataSourceConfig dc = dcs[i];
                if (dataSourceConfigs.containsKey(dc)) {
                    String name = dataSourceConfigs.get(dc);
                    if (Strings.isBlank(dc.getName())) {
                        throw new Exception("数据源名称不能为空");
                    }
                    if (!name.equalsIgnoreCase(dc.getName())) {
                        throw new Exception("【" + dc.getName() + "】数据源已经存在,请引用该名称:" + dataSourceConfigs.get(dc));
                    }
                }
                dataSourceConfigs.put(dc, dc.getName().toLowerCase());
                HikariConfig hc = new HikariConfig();
                hc.setPoolName(dc.getName());
                hc.setJdbcUrl(dc.getUrl());
                hc.setDriverClassName(dc.getDriver());
                hc.setUsername(dc.getUser());
                hc.setPassword(dc.getPwd());
                dataSources.put(dc.getName(), new HikariDataSource(hc));
            }
        }

    }

    private static void initEsSource(EsSourceConfig[] confs) throws Exception {
        if (confs != null && confs.length > 0) {
            for (int i = 0; i < confs.length; i++) {
                EsSourceConfig conf = confs[i];
                if (esSourceConfigs.containsKey(conf)) {
                    String name = esSourceConfigs.get(conf);
                    if (Strings.isBlank(conf.getName())) {
                        throw new Exception("ES数据源名称不能为空");
                    }
                    if (!name.equalsIgnoreCase(conf.getName())) {
                        throw new Exception("【" + conf.getName() + "】数据源已经存在,请引用该名称:" + esSourceConfigs.get(conf.getName()));
                    }
                }
                esSourceConfigs.put(conf, conf.getName().toLowerCase());
                ESClient client = ESClient.get(conf.getHost(), conf.getPort(), conf.getUser(), conf.getPwd());
                esSources.put(conf.getName(), client);
            }
        }

    }

}
