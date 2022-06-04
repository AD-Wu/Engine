package com.x.isearch.server.data.config.rule;

import com.x.isearch.server.data.config.converter.ConverterConfig;
import com.x.isearch.server.data.config.reader.TableReaderConfig;
import com.x.isearch.server.data.config.writer.ESWriterConfig;
import java.util.List;

/**
 * @author AD
 * @date 2022/3/24 20:21
 */
public class MoveRuleConfig {

    /**
     * 单表读取配置
     */
    private TableReaderConfig tableReaderConfig;

    /**
     * ES写入配置
     */
    private ESWriterConfig esWriterConfig;

    /**
     * 转换器配置
     */
    private List<ConverterConfig> converterConfigs;

    public TableReaderConfig getTableReaderConfig() {
        return tableReaderConfig;
    }

    public void setTableReaderConfig(TableReaderConfig tableReaderConfig) {
        this.tableReaderConfig = tableReaderConfig;
    }

    public ESWriterConfig getEsWriterConfig() {
        return esWriterConfig;
    }

    public void setEsWriterConfig(ESWriterConfig esWriterConfig) {
        this.esWriterConfig = esWriterConfig;
    }

    public List<ConverterConfig> getConverterConfigs() {
        return converterConfigs;
    }

    public void setConverterConfigs(List<ConverterConfig> converterConfigs) {
        this.converterConfigs = converterConfigs;
    }
}
