package com.x.isearch.server.reader;

import com.x.doraemon.Strings;
import com.x.isearch.server.core.IDataListener;
import com.x.isearch.server.core.IReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author AD
 * @date 2022/1/14 14:16
 */
public abstract class BaseSqlReader implements IReader<List<Map<String, Object>>> {

    // -------------------------- 成员变量 --------------------------

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected int limitRows = 1000;

    protected final DataSource dataSource;

    protected final String table;

    protected final String timeColumn;

    private int startIndex;

    private Comparable lastTime;

    // -------------------------- 构造方法 --------------------------

    /**
     * 构造方法
     * @param dataSource 数据源
     * @param table      数据表
     * @param timeColumn 增量字段(datetime|timestamp|comparable)
     * @throws Exception
     */
    protected BaseSqlReader(DataSource dataSource, String table, String timeColumn) throws Exception {
        this.dataSource = dataSource;
        this.table = table;
        this.timeColumn = timeColumn;
    }

    // -------------------------- 成员方法 --------------------------

    /**
     * 只读取时间字段不为空的数据
     * @param listener
     * @throws Exception
     */
    @Override
    public final void read(IDataListener<List<Map<String, Object>>> listener) throws Exception {
        long total = getTotal();
        // ----------------------- 删除数据 -----------------------
        if (total == 0) {
            reset();
            listener.deleteAll();
            log.info("数据库没有值,重置所有标识位,删除数据");
            return;
        }
        // ----------------------- 增量抽取 -----------------------
        // 判断时间字段是否有效
        Comparable lastTime = getLastTime();
        if (lastTime == null) {
            log.error("增量时间字段值为空,不进行数据抽取");
            return;
        }
        // 分页查询
        List<Map<String, Object>> rows = pageQuery(getPageSQL());
        // 设置分页查询标识位
        setFlag(rows);
        // 返回数据
        listener.add(rows);
    }

    // -------------------------- 抽象方法 --------------------------

    protected abstract String getPageSQL() throws Exception;

    // -------------------------- 保护方法 --------------------------

    protected Comparable getLastTime() throws Exception {
        if (lastTime != null) {
            return lastTime;
        }
        Object minValue = getMinValue();
        if (minValue != null) {
            this.lastTime = (Comparable) minValue;
        }
        return lastTime;
    }

    protected int getStartIndex() {
        return startIndex;
    }

    protected int getLimitRows() {
        return limitRows;
    }

    // -------------------------- 私有方法 --------------------------

    private void reset() {
        this.startIndex = 0;
        this.lastTime = null;
    }

    private void setFlag(List<Map<String, Object>> datas) {
        // 计算当前数据大小(可能与前一批数据存在重复)
        int size = datas.size();
        this.startIndex += size;
        if (size > 0) {
            Map<String, Object> row = datas.get(size - 1);
            Comparable o = (Comparable) row.get(timeColumn);
            this.lastTime = o;
        }

    }

    private List<Map<String, Object>> pageQuery(String sql) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            log.info("执行pageSQL:{}", sql);
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int count = md.getColumnCount();
            while (rs.next()) {
                int i = 1;
                Map<String, Object> row = new LinkedHashMap<>();
                while (i <= count) {
                    String column = md.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(column, value);
                    i++;
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private long getTotal() throws Exception {
        String countSql = "SELECT COUNT(*) COUNT FROM " + table.toUpperCase();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql);
             ResultSet rs = ps.executeQuery()) {
            log.info("执行countSQL:{}", countSql);
            while (rs.next()) {
                long count = rs.getLong("COUNT");
                log.info("countSQL结果:{}", count);
                return count;
            }
        }
        return 0;
    }

    private Object getMinValue() throws Exception {
        if (Strings.isNull(table) || Strings.isNull(timeColumn)) {
            throw new Exception("表名|时间字段为空,请检查配置");
        }
        String minSQL = "SELECT MIN(" + timeColumn + ") " + timeColumn + " FROM " + table;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(minSQL);
             ResultSet rs = ps.executeQuery()) {
            log.info("执行minSQL:{}", minSQL);
            while (rs.next()) {
                Object o = rs.getObject(timeColumn);
                log.info("minSQL结果:{}", o);
                return o;
            }
        }
        return null;
    }

}
