package com.x.isearch.server.converter.factory;

import com.x.doraemon.Strings;
import com.x.isearch.server.core.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/3/20 11:20
 */
public class RecursionSqlConverter extends BaseConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecursionSqlConverter.class);
    /**
     * 父列名
     */
    private String supColumn;
    /**
     * 子列名
     */
    private String subColumn;
    /**
     * 填充子列值所在的列名
     */
    private String subValueColumn;
    /**
     * 数据源
     */
    private DataSource source;
    /**
     * 表名
     */
    private String table;

    private String sql = "select {sub},{sup} from {table} where {sub} = ?";

    public RecursionSqlConverter(DataSource source, String table, String supColumn, String subColumn, String subValueColumn)
        throws Exception {
        this.source = source;
        this.table = table;
        this.supColumn = supColumn;
        this.subColumn = subColumn;
        this.subValueColumn = subValueColumn;
        checkNull(table, supColumn, subColumn, subColumn);
        this.sql = sql.replace("{sub}", subColumn).replace("{sup}", supColumn).replace("{table}", this.table);
    }

    @Override
    public void init(Context ctx) {
    }

    @Override
    public Map<String, Object> handle(Map<String, Object> map) throws Exception {
        if (!map.containsKey(subValueColumn)) {
            throw new Exception("列不存在,请检查配置:" + subValueColumn);
        }
        Object subColumnValue = map.get(subValueColumn);
        if (subColumnValue == null) {
            throw new Exception("值为空,请检查数据库记录,id:" + map.get("id"));
        }
        List<Object> supValues = new ArrayList<>();
        recursionQuery(subColumnValue, supValues);
        map.put(supColumn + "s", supValues);
        LOGGER.info("递归拦截器:id:{},字段:{},值:{}", map.get("id"), supColumn + "s", supValues);
        return map;
    }

    private void recursionQuery(Object subColumnValue, List<Object> supValues) {
        try (Connection conn = source.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, subColumnValue);
            try (ResultSet rs = ps.executeQuery()) {
                // 只获取一列结果
                if (rs.next()) {
                    // 父列值为空时,结束整个递归
                    Object supValue = rs.getObject(supColumn);
                    if (supValue != null) {
                        supValues.add(supValue);
                        recursionQuery(supValue, supValues);
                    } else {
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkNull(String... checks) throws Exception {
        for (int i = 0; i < checks.length; i++) {
            if (Strings.isBlank(checks[i])) {
                throw new Exception("递归sql转换器的值不能为空");
            }
        }
    }
}
