package com.x.isearch.server.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 数据搬运工
 * @author AD
 * @date 2022/1/13 14:18
 */
public class DataMover {

    private volatile boolean started;
    private final IReader reader;
    private final IWriter writer;
    private final ScheduledExecutorService executor;

    private IConverter[] converters;

    public DataMover(IReader reader, IWriter writer) {
        this.started = false;
        this.reader = reader;
        this.writer = writer;
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public synchronized void start() throws Exception {
        if (started) {
            return;
        }
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    reader.read(new IDataListener<List<Map<String, Object>>>() {

                        @Override
                        public int add(List<Map<String, Object>> rows) throws Exception {
                            List<Object> data = convert(rows);
                            writer.write(data);
                            return data.size();
                        }

                        @Override
                        public int delete(List<Map<String, Object>> rows) throws Exception {
                            return 0;
                        }

                        @Override
                        public int deleteAll() throws Exception {
                            return 0;
                        }

                        @Override
                        public int update(List<Map<String, Object>> data) throws Exception {
                            return 0;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 5, 60, TimeUnit.SECONDS);
        started = true;
    }

    public synchronized void stop() {
        if (started) {
            executor.shutdown();
            started = false;
        }
    }

    public void setConverters(IConverter[] converters) {
        this.converters = converters;
    }

    // -------------------------- 私有方法 --------------------------


    private List<Object> convert(List<Map<String, Object>> rows) throws Exception {
        List<Object> writes = new ArrayList<>();
        for (int i = 0, c = rows.size(); i < c; i++) {
            Object row = rows.get(i);
            if (converters != null && converters.length > 0) {
                int index = 0;
                while (index < converters.length) {
                    IConverter converter = converters[index++];
                    row = converter.handle(row);
                }
            }
            writes.add(row);
        }
        return writes;
    }

}
