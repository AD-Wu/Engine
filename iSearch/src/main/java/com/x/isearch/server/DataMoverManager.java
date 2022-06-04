package com.x.isearch.server;

import com.x.doraemon.Strings;
import com.x.doraemon.datasource.DataSources;
import com.x.isearch.server.core.Context;
import com.x.isearch.server.core.DataMover;
import com.x.isearch.server.core.IConverter;
import com.x.isearch.server.core.IReader;
import com.x.isearch.server.core.IWriter;
import com.x.isearch.server.data.config.DataMoveConfig;
import com.x.isearch.server.data.config.converter.ConverterConfig;
import com.x.isearch.server.data.config.reader.TableReaderConfig;
import com.x.isearch.server.data.config.rule.MoveRuleConfig;
import com.x.isearch.server.data.config.writer.ESWriterConfig;
import com.x.isearch.server.es.ESClient;
import com.x.isearch.server.reader.facotry.MysqlReader;
import com.x.isearch.server.writer.ESWriter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * @author AD
 * @date 2022/3/24 16:44
 */
public class DataMoverManager {

    public static synchronized DataMover[] create(DataMoveConfig conf) throws Exception {
        // 初始化数据源(共享)
        Context.initSource(conf);
        // 获取规则配置
        MoveRuleConfig[] rules = conf.getMoveRuleConfigs();
        // 初始化DataMove数组
        List<DataMover> movers = new ArrayList<>();
        for (int i = 0; i < rules.length; i++) {
            MoveRuleConfig rule = rules[i];
            // 初始化Reader信息
            IReader reader = initTableReader(rule.getTableReaderConfig());
            // 初始化Writer信息
            IWriter writer = initESWriter(rule.getEsWriterConfig(), rule.getTableReaderConfig().getTable());
            // 初始化Converter信息([0~n]个)
            List<IConverter> converters = initConverter(rule.getConverterConfigs());
            DataMover mover = new DataMover(reader, writer);
            mover.setConverters(converters.toArray(new IConverter[0]));
            movers.add(mover);
        }
        return movers.toArray(new DataMover[0]);

    }

    private static IReader initTableReader(TableReaderConfig conf) throws Exception {
        String name = conf.getDataSourceName();
        String table = conf.getTable();
        String timeCol = conf.getTimeColumn();
        DataSource src = Context.getDataSource(name);
        if (src == null) {
            throw new Exception("数据源不存在:" + name);
        }
        if (Strings.isBlank(table)) {
            throw new Exception("表名不能为空");
        }
        if (Strings.isBlank(timeCol)) {
            throw new Exception("增量时间字段不能为空");
        }
        DataSources ds = new DataSources(src);
        String type = ds.getDatabaseInfo().getType();
        if ("mysql".equalsIgnoreCase(type)) {
            return new MysqlReader(src, table, timeCol);
        } else {
            throw new Exception("暂不支持该类型的数据库:" + type);
        }
    }

    private static ESWriter initESWriter(ESWriterConfig conf, String table) throws Exception {
        String name = conf.getEsSourceName();
        ESClient src = Context.getESClient(name);
        if (src == null) {
            throw new Exception("ES数据源不存在:" + name);
        }
        String index = conf.getIndex();
        if (Strings.isBlank(index)) {
            index = table;
        }
        return new ESWriter(src, index);
    }

    private static List<IConverter> initConverter(List<ConverterConfig> confs) throws Exception {
        if (confs != null && confs.size() > 0) {
            List<IConverter> converters = new ArrayList<>();
            for (int i = 0, c = confs.size(); i < c; i++) {
                ConverterConfig conf = confs.get(i);
                String className = conf.getClassName();
                Class<?> clazz = Class.forName(className);
                if (!IConverter.class.isAssignableFrom(clazz)) {
                    throw new Exception(className + "转换类请实现接口:" + IConverter.class.getName() + ",且需提供无参构造方法");
                }
                IConverter converter = (IConverter) clazz.newInstance();
                // 初始化成员变量
                converter.init(new Context(conf));
            }
            return converters;
        }
        return null;
    }

}
