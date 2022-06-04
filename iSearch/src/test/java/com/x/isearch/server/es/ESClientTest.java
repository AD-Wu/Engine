package com.x.isearch.server.es;

import com.x.doraemon.DateTimes;
import com.x.doraemon.Jsons;
import com.x.isearch.server.es.data.Mapping;
import com.x.isearch.server.es.data.SearchParam;
import com.x.isearch.server.es.data.SearchType;
import com.x.isearch.server.es.data.Setting;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.core.MainResponse.Version;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
import org.elasticsearch.common.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author AD
 * @date 2022/2/19 12:40
 */
// @SpringBootTest
class ESClientTest {

    private static final String NEWS = "news";

    private static final String NEWS_1 = "news_1";

    // 主键
    private static final String id = "id";
    // 标题
    private static final String title = "title";
    // 内容
    private static final String content = "content";
    // 关键字(数组)
    private static final String keywords = "keywords";
    // 作者
    private static final String author = "author";
    // 创建时间
    private static final String createTime = "createTime";
    // 阅读记录(nested,包括阅读人和阅读次数)
    private static final String readRecord = "readRecord";


    private ESClient client;

    @BeforeEach
    void before() {
        client = ESClient.builder("localhost", 9200).build();
    }

    @Test
    void existIndex() throws IOException {
        boolean b = client.existIndex(NEWS);
        System.out.println(b);
    }

    @Test
    void createIndex() throws Exception {
        try (ESClient helper = ESClient.builder("localhost", 9200).build()) {
            // 判断是否存在
            if (helper.existIndex(NEWS)) {
                // 删除
                helper.deleteIndex(NEWS);
            }
            List<Mapping> ms = new ArrayList<>();
            ms.addAll(Mapping.newText(title, SearchType.SIMPLE, SearchType.COMPLETION));
            ms.addAll(Mapping.newText(content));
            ms.addAll(Mapping.newText(keywords, SearchType.SIMPLE, SearchType.COMPLETION));
            ms.addAll(Mapping.newText(author, SearchType.SIMPLE));
            ms.add(Mapping.newDateTime(createTime));
            List<Mapping> innerMappings = Mapping.newText("name");
            innerMappings.add(Mapping.newInteger("count"));
            ms.add(Mapping.newNested(readRecord, innerMappings.toArray(new Mapping[0])));
            // 创建
            helper.createIndex(NEWS, Setting.defaultSetting(), ms.toArray(new Mapping[0]));
            // 查询
            GetIndexResponse index = helper.getIndex(NEWS);
            // 查询settings
            Settings settings = index.getSettings().get(NEWS);
            System.out.println("settings=" + settings);
            // 查询mappings
            Map<String, MappingMetadata> mappings = index.getMappings();
            Iterator<Entry<String, MappingMetadata>> it = mappings.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, MappingMetadata> next = it.next();
                System.out.println(next.getKey());
                System.out.println(Jsons.toJson(next.getValue().sourceAsMap()));
                System.out.println("---------------------");
            }
            putDocs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void putDocs() throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        News[] newses = News.news();
        for (int i = 0; i < newses.length; i++) {
            News news = newses[i];
            Map<String, Object> row = new HashMap<>();
            row.put(id, news.getId());
            row.put(title, news.getTitle());
            row.put(content, news.getContent());
            row.put(keywords, news.getKeywords());
            row.put(author, news.getAuthor());
            row.put(createTime, news.getCreateTime());
            row.put(readRecord, news.getReadRecords());
            rows.add(row);
        }
        client.putDocs(NEWS, rows);
    }

    @Test
    void getColumnType() throws IOException {
        Map<String, String> columns = client.getColumnType(NEWS);
        System.out.println("--------------------------------------------");
        Iterator<Entry<String, String>> it = columns.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> next = it.next();
            System.out.printf("%-20s %20s\n", next.getKey(), next.getValue());
        }
        System.out.println("--------------------------------------------");

    }

    @Test
    void boolSearch() throws Exception {
        SearchParam sp = new SearchParam(new String[]{NEWS, NEWS_1}, "美国", title, keywords, content, author);
        SearchResponse resp = client.boolSearch(sp);
        Map<String, Map<String, Set<String>>> result = client.getHits(resp);
        result.forEach((k, v) -> {
            System.out.println("-------------------");
            System.out.println(k);
            v.forEach((a, bs) -> {
                System.out.print("\t" + a + "\n");
                bs.forEach(b -> {
                    System.out.print("\t\t" + b + "\n");
                });
            });
            System.out.println("-------------------");
        });
    }

    @Test
    void completionSearch() throws Exception {
        String[] options = client.completion(new SearchParam(new String[]{NEWS, NEWS_1}, "g", title, keywords));
        for (String option : options) {
            System.out.println(option);
        }
    }

    @Test
    void wildcardSearch() throws Exception {
        String[] authors = client.wildcardSearch(new SearchParam(new String[]{NEWS}, "l", author, title));
        for (String author : authors) {
            System.out.println(author);
        }

    }

    @Test
    void getIndices() throws IOException {
        String[] indices = client.getIndices();
        for (String index : indices) {
            System.out.println(index);
        }
    }

    @Test
    void getIndicesInfo() throws IOException {
        String[] indices = client.getIndices();
        for (String index : indices) {
            System.out.println(index);
        }
    }

    @Test
    void deleteIndex() throws IOException {
        boolean delete = client.deleteIndex("sunday");
        System.out.println(delete);
    }

    @Test
    void getESInfo() throws Exception {
        MainResponse info = client.getESInfo();
        System.out.println(info.getClusterName());
        System.out.println(info.getClusterUuid());
        System.out.println(info.getNodeName());

        Version v = info.getVersion();
        System.out.println(v.getNumber());
        String buildDate = v.getBuildDate();
        LocalDateTime format = DateTimes.toLocalDateTime(buildDate);
        System.out.println(v.getBuildDate());
        System.out.println(format);
    }

    @Test
    void deleteDoc() {

    }

    @Test
    void getIndex() throws IOException {

    }

    @Test
    void getDoc() {
    }

    public static class News {

        // 主键
        private int id;
        // 标题
        private String title;
        // 内容
        private String content;
        // 关键字(数组)
        private String[] keywords;
        // 作者
        private String author;
        // 创建时间
        private String createTime;
        // 阅读记录(nested,包括阅读人和阅读次数)
        private List<Map<String, Object>> readRecords;

        static List<String> titles = new ArrayList<>();

        static {
            titles.add("乔布斯的硅谷奇迹");
            titles.add("钢铁侠马斯克是一个科技狂魔");
            titles.add("创业教父马云的传奇");
            titles.add("Google的撅起之路");
        }

        static List<String> contents = new ArrayList<>();

        static {
            contents.add("乔布斯出生于美国,热爱电子产品,缔造了苹果帝国,死于癌症");
            contents.add("钢铁侠马斯克出生于美国,热爱科技,偶像是特斯拉,特斯拉电动车很牛");
            contents.add("创业教父马云出生于杭州,创办了阿里巴巴,淘宝惠及千万家");
            contents.add("Google是美国的一家有趣的公司,创始人毕业于斯坦福大学,谷歌的餐饮很丰富");
        }

        static List<String[]> keywordses = new ArrayList<>();

        static {
            keywordses.add(new String[]{"乔布斯", "癌症", "手机", "美国"});
            keywordses.add(new String[]{"马斯克", "特斯拉", "电动车", "美国"});
            keywordses.add(new String[]{"马云", "杭州", "阿里巴巴", "淘宝"});
            keywordses.add(new String[]{"谷歌", "斯坦福", "有趣"});
        }

        static List<String> authors = new ArrayList<>();

        static {
            authors.add("张三");
            authors.add("李四");
            authors.add("王五");
            authors.add("赵六");
        }

        static List<String> createTimes = new ArrayList<>();

        static {
            createTimes.add("2019-02-02 12:00:00");
            createTimes.add("2020-02-02 12:00:00");
            createTimes.add("2021-02-02 12:00:00");
            createTimes.add("2022-02-02 12:00:00");
        }

        static List<List<Map<String, Object>>> readRecordses = new ArrayList<>();

        static {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("name", "张小三");
            map1.put("count", 3);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("name", "李小四");
            map2.put("count", 4);
            Map<String, Object> map3 = new HashMap<>();
            map3.put("name", "王小五");
            map3.put("count", 5);
            Map<String, Object> map4 = new HashMap<>();
            map4.put("name", "赵小六");
            map4.put("count", 6);
            List<Map<String, Object>> list1 = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            List<Map<String, Object>> list3 = new ArrayList<>();
            List<Map<String, Object>> list4 = new ArrayList<>();
            list1.add(map1);
            list1.add(map2);

            list2.add(map2);
            list2.add(map3);

            list3.add(map3);
            list3.add(map4);

            list4.add(map1);
            list4.add(map4);

            readRecordses.add(list1);
            readRecordses.add(list2);
            readRecordses.add(list3);
            readRecordses.add(list4);
        }

        public static News[] news() {
            News[] newses = new News[4];
            for (int i = 0; i < 4; i++) {
                News news = new News();
                news.setId(i);
                news.setTitle(titles.get(i));
                news.setContent(contents.get(i));
                news.setKeywords(keywordses.get(i));
                news.setAuthor(authors.get(i));
                news.setCreateTime(createTimes.get(i));
                news.setReadRecord(readRecordses.get(i));
                newses[i] = news;
            }
            return newses;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String[] getKeywords() {
            return keywords;
        }

        public void setKeywords(String[] keywords) {
            this.keywords = keywords;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public List<Map<String, Object>> getReadRecords() {
            return readRecords;
        }

        public void setReadRecord(List<Map<String, Object>> readRecords) {
            this.readRecords = readRecords;
        }
    }
}
