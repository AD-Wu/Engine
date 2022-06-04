package com.x.isearch.util;

import com.x.doraemon.datasource.DataSources;
import com.x.doraemon.datasource.data.DatabaseInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试类的方法:public;不能有返回值;入参要有实际参数,不能写个占位符;
 *
 * @author AD
 * @date 2022/1/12 0:33
 */
@SpringBootTest
class DataSourcesTest {

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Test
    public void executeQuery() throws Exception {
        String sql = "select * from word where modify_time >='2017-05-16 14:31:16.000' ORDER BY modify_time ASC LIMIT 0,1000";
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {
            List<Map<String, Object>> datas = new ArrayList<>();
            while (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                int i = 1;
                Map<String, Object> row = new LinkedHashMap<>();
                while (i <= count) {
                    String label = md.getColumnLabel(i);
                    Object o = rs.getObject(i);
                    row.put(label, o);
                    i++;
                }
                datas.add(row);
            }
            for (int i = 0, N = datas.size(); i < N; i++) {
                System.out.println(datas.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDatabaseInfo() throws SQLException {
        DataSources dss = new DataSources(dataSource);
        DatabaseInfo info = dss.getDatabaseInfo();
        System.out.println(info);


    }

}
