package com.x.isearch.server.data.config;

import com.x.isearch.server.data.config.rule.MoveRuleConfig;
import com.x.isearch.server.data.config.source.DataSourceConfig;
import com.x.isearch.server.data.config.source.EsSourceConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author AD
 * @date 2022/3/17 20:14
 */
public class DataMoveConfig {

    // --------------------------- 成员变量 ---------------------------

    /**
     * 数据源配置
     */
    private DataSourceConfig[] dataSourceConfigs;
    /**
     * ES数据源配置
     */
    private EsSourceConfig[] esSourceConfigs;
    /**
     * 抽取规则配置
     */
    private MoveRuleConfig[] moveRuleConfigs;

    // --------------------------- 成员方法 ---------------------------

    public DataSourceConfig[] getDataSourceConfigs() {
        return dataSourceConfigs;
    }

    public void setDataSourceConfigs(DataSourceConfig[] dataSourceConfigs) {
        this.dataSourceConfigs = dataSourceConfigs;
    }

    public EsSourceConfig[] getEsSourceConfigs() {
        return esSourceConfigs;
    }

    public void setEsSourceConfigs(EsSourceConfig[] esSourceConfigs) {
        this.esSourceConfigs = esSourceConfigs;
    }

    public MoveRuleConfig[] getMoveRuleConfigs() {
        return moveRuleConfigs;
    }

    public void setMoveRuleConfigs(MoveRuleConfig[] moveRuleConfigs) {
        this.moveRuleConfigs = moveRuleConfigs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
