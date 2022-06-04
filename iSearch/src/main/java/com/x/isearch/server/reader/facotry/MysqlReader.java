package com.x.isearch.server.reader.facotry;

import com.x.isearch.server.reader.BaseSqlReader;
import javax.sql.DataSource;

/**
 * @author AD
 * @date 2022/2/18 19:57
 */
public class MysqlReader extends BaseSqlReader {

    /**
     * 构造方法
     *
     * @param dataSource 数据源
     * @param table      数据表
     * @param timeColumn 增量字段(datetime|timestamp|comparable)
     * @throws Exception
     */
    public MysqlReader(DataSource dataSource, String table, String timeColumn) throws Exception {
        super(dataSource, table, timeColumn);
    }

    @Override
    protected String getPageSQL() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(table);
        sb.append(" where ").append(timeColumn).append(" >= '").append(getLastTime().toString()).append("'");
        sb.append(" order by ").append(timeColumn).append(" asc");
        sb.append(" limit ").append(getStartIndex()).append(",").append(getLimitRows());
        return sb.toString().toUpperCase();
    }

    public void setLimitRows(int limitRows){
        this.limitRows = limitRows;
    }
}
