package com.x.doraemon.datasource;

import com.x.doraemon.Converts;
import com.x.doraemon.Strings;
import com.x.doraemon.datasource.data.ColumnInfo;
import com.x.doraemon.datasource.data.DatabaseInfo;
import com.x.doraemon.datasource.data.TableInfo;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * @author AD
 * @date 2022/1/12 13:23
 */
public class DataSources {

    // ------------------------------ 私有常量 ------------------------------

    private static final String SHOW_COLUMN_SQL = "SHOW FULL COLUMNS FROM {TABLE}";

    private static final String SELECT_SQL = "SELECT * FROM {TABLE} LIMIT 0";

    // ------------------------------ 成员变量 ------------------------------

    private final DataSource dataSource;

    // ------------------------------ 构造方法 ------------------------------

    /**
     * 构造方法
     *
     * @param dataSource 数据源对象
     */
    public DataSources(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    // ------------------------------ 公共方法 ------------------------------

    /**
     * 获取数据库信息
     *
     * @return
     * @throws SQLException
     */
    public DatabaseInfo getDatabaseInfo() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData md = conn.getMetaData();
            DatabaseInfo info = new DatabaseInfo();
            info.setType(md.getDatabaseProductName());
            info.setVersion(md.getDatabaseProductVersion());
            info.setDriverClassName(identifyDriver(md.getDriverName()));
            info.setDriverVersion(md.getDriverVersion());
            info.setUrl(md.getURL());
            info.setUser(md.getUserName());
            info.setCatalog(conn.getCatalog());
            return info;
        }
    }

    /**
     * 获取表信息
     *
     * @param table 数据库表名
     * @return
     */
    public TableInfo getTableInfo(String table) throws Exception {
        if (Strings.isNull(table)) {
            return null;
        }
        try (Connection conn = dataSource.getConnection();
             //从元数据中获取到所有的表名
             ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, table, new String[]{"TABLE"})) {

            while (rs.next()) {
                if (Strings.endsWithIgnoreCase(table, rs.getString("TABLE_NAME"))) {
                    ColumnInfo[] cis = getColumnInfos(table);
                    TableInfo info = new TableInfo();
                    info.setName(table);
                    info.setColumns(cis);
                }
            }
            return null;
        }
    }

    /**
     * 获取所有的表信息
     *
     * @return
     */
    public TableInfo[] getTableInfos() throws Exception {
        List<TableInfo> tis = new ArrayList<>();
        // 获取所有表
        String[] tables = getTableNames();
        // 遍历
        for (int i = 0, N = tables.length; i < N; i++) {
            String table = tables[i];
            TableInfo ti = new TableInfo();
            ti.setName(table);
            // 获取所有列信息
            ti.setColumns(getColumnInfos(table));
            tis.add(ti);
        }
        return tis.toArray(new TableInfo[0]);
    }

    // ------------------------------ 私有方法 ------------------------------

    /**
     * 获取数据库下的所有表名
     */
    private String[] getTableNames() throws Exception {
        List<String> names = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             //从元数据中获取到所有的表名
             ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, null, new String[]{"TABLE"})) {
            while (rs.next()) {
                names.add(rs.getString("TABLE_NAME"));
            }
        }
        return names.toArray(new String[0]);
    }

    /**
     * 获取表中所有字段名称
     *
     * @param table 表名
     * @return
     */
    private ColumnInfo[] getColumnInfos(String table) throws Exception {
        List<ColumnInfo> infos = new ArrayList<>();
        String selectSql = SELECT_SQL.replace("{TABLE}", table);
        String columnSql = SHOW_COLUMN_SQL.replace("{TABLE}", table);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql);
             PreparedStatement pss = conn.prepareStatement(columnSql);
             ResultSet rs = ps.executeQuery(columnSql)) {
            //结果集元数据
            ResultSetMetaData md = ps.getMetaData();
            //表列数
            int size = md.getColumnCount();
            int i = 0;
            while (rs.next()) {
                i++;
                ColumnInfo info = new ColumnInfo();
                info.setName(md.getColumnName(i));
                info.setType(md.getColumnType(i));
                info.setTypeName(md.getColumnTypeName(i));
                info.setClassName(md.getColumnClassName(i));
                info.setNullable(Converts.toBoolean(md.isNullable(i)));
                info.setAutoIncrement(md.isAutoIncrement(i));
                info.setComment(rs.getString("Comment"));
                info.setDefaultValue(rs.getObject("Default"));
                info.setPrimaryKey(Converts.toBoolean(rs.getString("Key"), "PRI"));
                infos.add(info);
            }
        }
        return infos.toArray(new ColumnInfo[0]);
    }

    private static String identifyDriver(String db) {
        if (Strings.isBlank(db)) {
            return null;
        } else {
            db = db.toLowerCase().replace(" ","");
            String driver = null;
            if (db.contains("mysql")) {
                try {
                    Class<?> clz = Class.forName("com.mysql.cj.jdbc.Driver");
                    driver = "com.mysql.cj.jdbc.Driver";
                } catch (ClassNotFoundException e) {
                    driver = "com.mysql.jdbc.Driver";
                }
            } else if (db.contains("oracle")) {
                try {
                    Class<?> clz = Class.forName("oracle.jdbc.OracleDriver");
                    driver = "oracle.jdbc.OracleDriver";
                } catch (ClassNotFoundException e) {
                    driver = "oracle.jdbc.driver.OracleDriver";
                }
            } else if (db.contains("postgresql")) {
                driver = "org.postgresql.Driver";
            } else if (db.contains("sqlite")) {
                driver = "org.sqlite.JDBC";
            } else if (db.contains("sqlserver")) {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            } else if (db.contains("hive")) {
                driver = "org.apache.hadoop.hive.jdbc.HiveDriver";
            } else if (db.contains("h2")) {
                driver = "org.h2.Driver";
            } else if (db.startsWith("jdbc:derby://")) {
                driver = "org.apache.derby.jdbc.ClientDriver";
            } else if (db.contains("derby")) {
                driver = "org.apache.derby.jdbc.EmbeddedDriver";
            } else if (db.contains("hsqldb")) {
                driver = "org.hsqldb.jdbc.JDBCDriver";
            } else if (db.contains("dm")) {
                driver = "dm.jdbc.driver.DmDriver";
            }
            return driver;
        }
    }

}
