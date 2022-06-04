package com.x.isearch.server.converter.factory;

import com.x.isearch.server.core.Context;
import com.x.isearch.server.data.config.converter.ConverterConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * @author AD
 * @date 2022/3/17 1:58
 */
public class SqlConverter extends BaseConverter {

    // /**
    //  * 数据源
    //  */
    // private DataSource source;

    /**
     * 数据源名,唯一标识
     */
    private String dataSourceName;
    /**
     * 要执行的sql查询语句
     */
    private String sql;
    /**
     * 用于填充sql中"?"的值所在的列(来自于上一个数据集)
     */
    private String[] valueColumns;
    //
    // public SqlConverter(SqlConfig conf) {
    //     this.dataSourceName = conf.getDataSourceName();
    //     this.sql = conf.getSql();
    //     this.valueColumns = conf.getValueColumns();
    // }
    //
    // public SqlConverter(DataSource source, String sql, String[] valueColumns) {
    //     // this.source = source;
    //     this.sql = sql;
    //     this.valueColumns = valueColumns;
    // }

    @Override
    public void init(Context ctx) {
        ConverterConfig conf = ctx.getConverterConfig();
        this.dataSourceName = conf.getString(SqlConverter::getDataSourceName);
        // this.source = ctx.getDataSource(dataSourceName);
        this.sql = conf.getString(SqlConverter::getSql);
        this.valueColumns = conf.getArray(SqlConverter::getValueColumns);
    }

    @Override
    public Map<String, Object> handle(Map<String, Object> row) throws Exception {
        // 先检查列是否有效
        if (!isColumnValid(sql, valueColumns, row)) {
            // 配置有误,返回原数据集
            return row;
        }
        // 获取sql中'?'的值
        List<Object> values = getValues(row);

        DataSource source = Context.getDataSource(this.dataSourceName);
        if (source == null) {
            return row;
        }

        // 查询数据
        try (Connection conn = source.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0, k = 1, c = values.size(); i < c; i++, k++) {
                ps.setObject(k, values.get(i));
            }
            List<String> cols = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                // 获取所有标签名(没有别名时,则为列名)
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    cols.add(md.getColumnLabel(i));
                }
                // 获取所有列值
                List<List<Object>> colValues = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    List<Object> vs = new ArrayList<>();
                    while (rs.next()) {
                        vs.add(rs.getObject(i));
                    }
                    colValues.add(vs);
                    rs.first();
                }
                // 将列值以数组的方式存入map
                for (int i = 0, c = cols.size(); i < c; i++) {
                    String col = cols.get(i);
                    List<Object> vs = colValues.get(i);
                    if (vs.size() == 0) {
                        row.put(col, null);
                    } else if (vs.size() == 1) {
                        row.put(col, vs.get(0));
                    } else {
                        row.put(col, vs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }

    private boolean isColumnValid(String sql, String[] columns, Map<String, Object> row) throws Exception {
        // 判断'?'与字段个数是否相等
        int count = 0;
        char[] chars = sql.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if ("?".equals(c)) {
                count++;
            }
        }
        if (count != columns.length) {
            throw new Exception("sql语句中的'?'与字段个数不匹配");
        }
        // 判断字段是否存在于数据集中
        for (String column : columns) {
            if (!row.containsKey(column)) {
                throw new Exception("字段不存在于数据集中:" + column);
            }
        }
        return true;
    }

    private List<Object> getValues(Map<String, Object> row) {
        List<Object> params = new ArrayList<>();
        for (String column : valueColumns) {
            Object v = row.get(column);
            if (v != null) {
                params.add(v);
            }
        }
        return params;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getSql() {
        return sql;
    }

    public String[] getValueColumns() {
        return valueColumns;
    }
}
