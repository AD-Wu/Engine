package com.x.isearch.server.writer;

import com.x.isearch.server.core.IWriter;
import com.x.isearch.server.es.ESClient;
import java.util.List;
import java.util.Map;

/**
 * @author AD
 * @date 2022/2/18 22:06
 */
public class ESWriter implements IWriter<List<Map<String, Object>>> {

    private final ESClient client;
    private final String index;

    public ESWriter(ESClient client, String index) {
        this.client = client;
        this.index = index;
    }

    @Override
    public void write(List<Map<String, Object>> rows) throws Exception {
        client.putDocs(index, rows);
    }
}
