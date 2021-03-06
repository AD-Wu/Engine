package com.x.isearch.server.es;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.x.doraemon.Arrayx;
import com.x.doraemon.Strings;
import com.x.isearch.server.es.data.ESType;
import com.x.isearch.server.es.data.Mapping;
import com.x.isearch.server.es.data.SearchParam;
import com.x.isearch.server.es.data.SearchParam.ColumnFilter;
import com.x.isearch.server.es.data.SearchParam.Highlight;
import com.x.isearch.server.es.data.SearchParam.SearchColumn;
import com.x.isearch.server.es.data.SearchParam.SearchFilter;
import com.x.isearch.server.es.data.Setting;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry.Option;

/**
 * @author AD
 * @date 2022/2/19 1:24
 */
public class ESClient implements Closeable {

    private static Map<String, ESClient> clients;


    // -------------------------- ???????????? --------------------------
    private final String host;
    private final int port;
    private final RestHighLevelClient client;
    private final Map<String, Map<String, String>> indexColumn;

    // -------------------------- ???????????? --------------------------

    /**
     * ????????????
     * @param builder ?????????
     */
    public ESClient(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.indexColumn = new ConcurrentHashMap<>();
        this.client = new RestHighLevelClient(builder.clientBuilder);
    }

    // -------------------------- ???????????? --------------------------
    public static ESClient get(String host, int port) {
        return get(host, port, null, null);
    }

    public static ESClient get(String host, int port, String user, String pwd) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            String key = addr.getHostAddress() + ":" + port;
            if (clients.containsKey(key)) {
                return clients.get(key);
            } else {
                Builder builder = builder(host, port);
                builder.timeout(30000);
                if (Strings.isNotBlank(user)) {
                    builder.password(user, pwd);
                }
                ESClient client = builder.build();
                clients.put(key, client);
                return client;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ?????????
     * @param host
     * @param port
     * @return
     */
    public static Builder builder(String host, int port) {
        return new Builder(host, port);
    }

    // -------------------------- ???????????? --------------------------

    /**
     * ??????ES??????
     * @return
     * @throws IOException
     */
    public MainResponse getESInfo() throws IOException {
        return client.info(RequestOptions.DEFAULT);
    }

    /**
     * ?????????????????????
     * @return
     * @throws IOException
     */
    public String[] getIndices() throws IOException {
        GetSettingsResponse resp = client.indices().getSettings(new GetSettingsRequest(), RequestOptions.DEFAULT);
        Iterator<ObjectObjectCursor<String, Settings>> it = resp.getIndexToSettings().iterator();
        List<String> indices = new ArrayList<>();
        while (it.hasNext()) {
            ObjectObjectCursor<String, Settings> next = it.next();
            String index = next.key;
            if (!index.startsWith(".")) {
                indices.add(index);
            }
        }
        return indices.toArray(new String[0]);
    }

    /**
     * ????????????????????????
     * @param index ?????????(??????????????????:??????|??????,??????:"_"|"-")
     * @return
     * @throws IOException
     */
    public boolean existIndex(String index) throws IOException {
        boolean exists = client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        return exists;
    }

    /**
     * ????????????
     * @param index ?????????
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String index, Setting setting, Mapping[] mappings) throws Exception {
        Settings settings = Settings.builder().loadFromMap(setting.getProperties()).build();
        return createIndex(index, settings, mappings);
    }

    /**
     * ????????????
     * @param index    ?????????
     * @param settings ??????(????????????????????????,????????????1)
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String index, Settings settings, Mapping[] mappings) throws Exception {
        if (Strings.isNull(index)) {
            throw new Exception("?????????????????????");
        }
        if (settings == null) {
            throw new Exception("????????????????????????????????????");
        }
        if (Arrayx.isEmpty(mappings)) {
            throw new Exception("????????????????????????");
        }
        if (existIndex(index)) {
            throw new Exception("???????????????:" + index);
        }
        CreateIndexRequest req = new CreateIndexRequest(index);
        req.settings(settings);
        req.mapping(buildMappings(mappings));
        CreateIndexResponse resp = client.indices().create(req, RequestOptions.DEFAULT);
        resp.index();
        synchronized (this) {
            indexColumn.put(index, getColumnType(index));
        }
        return resp;
    }

    /**
     * ????????????
     * @param index ?????????
     * @return
     * @throws IOException
     */
    public boolean deleteIndex(String index) throws IOException {
        if (!existIndex(index)) {
            return true;
        }
        synchronized (this) {
            indexColumn.remove(index);
        }
        AcknowledgedResponse resp = client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        return resp.isAcknowledged();
    }

    /**
     * ????????????
     * @param index ?????????
     * @param id    ??????id
     * @return
     * @throws IOException
     */
    public DeleteResponse deleteDoc(String index, String id) throws Exception {
        if (!existIndex(index)) {
            throw new Exception("???????????????:" + index);
        }
        DeleteResponse resp = client.delete(new DeleteRequest(index).id(id), RequestOptions.DEFAULT);
        return resp;
    }

    /**
     * ??????????????????
     * @param index ?????????
     * @return
     * @throws IOException
     */
    public GetIndexResponse getIndex(String index) throws Exception {
        if (!existIndex(index)) {
            throw new Exception("???????????????:" + index);
        }
        GetIndexResponse resp = client.indices().get(new GetIndexRequest(index), RequestOptions.DEFAULT);
        return resp;
    }

    /**
     * ??????id??????????????????
     * @param index ?????????
     * @param id    ??????
     * @return
     * @throws IOException
     */
    public GetResponse getDoc(String index, String id) throws Exception {
        if (!existIndex(index)) {
            throw new Exception("???????????????:" + index);
        }
        GetResponse resp = client.get(new GetRequest(index, id), RequestOptions.DEFAULT);
        return resp;
    }

    /**
     * ??????????????????????????????(ES???????????????,????????????????????????????????????)
     * @param index ?????????
     * @param rows  ????????????
     * @return
     * @throws IOException
     */
    public BulkResponse putDocs(String index, List<Map<String, Object>> rows) throws Exception {
        // ????????????????????????
        if (!existIndex(index)) {
            throw new Exception("???????????????:" + index);
        }
        // ??????????????????????????????
        Map<String, String> mapping = getColumnType(index);
        // ???????????????-simple???-completion?????????
        Set<String> autoColumns = mapping.keySet().stream().filter(c -> {
            return c.contains(Mapping.SIMPLE_SEARCH_SUFFIX) || c.contains(Mapping.COMPLETION_SEARCH_SUFFIX);
        }).collect(Collectors.toSet());
        BulkRequest bulkReq = new BulkRequest();
        for (int i = 0, c = rows.size(); i < c; i++) {
            // ????????????
            IndexRequest req = new IndexRequest(index);
            // ??????id
            Map<String, Object> row = rows.get(i);
            Object id = row.get("id");
            if (id != null && id.toString().trim().length() > 0) {
                req.id(id.toString());
            }
            // ??????????????????(???????????????????????????)
            Map<String, Object> newRow = new LinkedHashMap<>();
            // ?????????mapping?????????????????????,??????es??????????????????
            Iterator<Entry<String, Object>> it = row.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> next = it.next();
                if (!mapping.containsKey(next.getKey())) {
                    it.remove();
                } else {
                    newRow.put(next.getKey(), next.getValue());
                    for (String ac : autoColumns) {
                        if (ac.startsWith(next.getKey()) && !ac.equals(next.getKey())) {
                            newRow.put(ac, next.getValue());
                        }
                    }
                }
            }
            req.source(newRow, XContentType.JSON);
            bulkReq.add(req);
        }
        BulkResponse resp = client.bulk(bulkReq, RequestOptions.DEFAULT);
        return resp;
    }

    /**
     * ????????????????????????
     * @param index ?????????
     * @return
     * @throws IOException
     */
    public Map<String, String> getColumnType(String index) throws IOException {
        Map<String, String> mapping = indexColumn.get(index);
        if (mapping != null) {
            return mapping;
        }
        mapping = new LinkedHashMap<>();
        GetMappingsRequest req = new GetMappingsRequest().indices(index);
        GetMappingsResponse resp = client.indices().getMapping(req, RequestOptions.DEFAULT);
        Map<String, Object> columns = (Map<String, Object>) resp.mappings().get(index).sourceAsMap().get("properties");
        flatColumn(columns, mapping, null);
        synchronized (this) {
            indexColumn.put(index, mapping);
        }
        return mapping;
    }


    /**
     * ????????????(??????????????????:{@link Mapping.COMPLETION_SEARCH_SUFFIX })
     * @param param
     * @return
     * @throws Exception
     */
    public String[] completion(SearchParam param) throws Exception {
        return completion(param, true);
    }

    /**
     * ????????????
     * @param param      ????????????
     * @param autoSuffix ??????????????????????????????
     * @return
     * @throws IOException
     */
    public String[] completion(SearchParam param, boolean autoSuffix) throws Exception {
        // 1.????????????????????????
        List<String> indices = Arrays.stream(param.getIndices()).distinct().collect(Collectors.toList());
        for (String index : indices) {
            if (!existIndex(index)) {
                throw new Exception("???????????????:" + index);
            }
        }
        // 2.?????????????????????,????????????????????????
        List<SearchColumn> columns = Arrays.stream(param.getSearchColumns()).distinct().collect(Collectors.toList());
        if (columns == null || columns.size() == 0) {
            throw new Exception("?????????????????????");
        }

        for (String index : indices) {
            // ????????????????????????
            Map<String, String> fields = getColumnType(index);
            // ?????????????????????????????????????????????
            for (SearchColumn sc : columns) {
                String column = sc.getColumn();
                if (autoSuffix && !column.endsWith(Mapping.COMPLETION_SEARCH_SUFFIX)) {
                    column = sc.getColumn() + Mapping.COMPLETION_SEARCH_SUFFIX;
                    sc.setColumn(column);
                }
                if (!fields.containsKey(column)) {
                    throw new Exception("?????????" + index + "?????????????????????:" + sc.getColumn());
                } else {
                    // ???????????????????????????????????????
                    String type = fields.get(column);
                    if (!ESType.COMPLETION.toString().toLowerCase().equals(type.toLowerCase())) {
                        throw new Exception("?????????????????????(completion):" + sc.getColumn());
                    }
                }
            }
        }
        Set<String> suggests = new LinkedHashSet<>();
        for (SearchColumn sc : param.getSearchColumns()) {
            SuggestionBuilder suggest = SuggestBuilders.completionSuggestion(sc.getColumn()).prefix(param.getSearchValue())
                .skipDuplicates(true).size(100);
            SearchRequest req = new SearchRequest(indices.toArray(new String[0]));
            req.source().suggest(new SuggestBuilder().addSuggestion("suggestion", suggest));
            SearchResponse resp = client.search(req, RequestOptions.DEFAULT);
            // ????????????(????????????????????????,??????????????????)
            CompletionSuggestion suggestion = resp.getSuggest().getSuggestion("suggestion");
            for (Option option : suggestion.getOptions()) {
                suggests.add(option.getText().toString());
            }
        }
        return suggests.toArray(new String[0]);
    }

    /**
     * bool??????
     * @param param ????????????
     * @return
     * @throws IOException
     */
    public SearchResponse boolSearch(SearchParam param) throws Exception {
        // ??????bool???????????????
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.??????????????????(must)
        MultiMatchQueryBuilder must = new MultiMatchQueryBuilder(param.getSearchValue());
        for (SearchColumn sc : param.getSearchColumns()) {
            must.field(sc.getColumn(), sc.getBoost());
        }
        boolQuery.must(must);
        // 2.??????????????????(filter)
        BoolQueryBuilder filters = QueryBuilders.boolQuery();
        for (SearchFilter sf : param.getSearchFilters()) {
            BoolQueryBuilder filter = QueryBuilders.boolQuery();
            for (Object value : sf.getValue()) {
                // add??????(??????????????????"?????????")
                filter.should(QueryBuilders.matchQuery(sf.getColumn(), value));
            }
            filters.should(filter);
        }
        boolQuery.filter(filters);
        SearchResponse resp = search(param, boolQuery);
        return resp;
    }


    /**
     * ????????????
     * @param param        ????????????
     * @param queryBuilder ???????????????
     * @return
     * @throws IOException
     */
    public SearchResponse search(SearchParam param, QueryBuilder queryBuilder) throws Exception {
        // 1.????????????????????????
        List<String> indices = Arrays.stream(param.getIndices()).distinct().collect(Collectors.toList());
        for (String index : indices) {
            if (!existIndex(index)) {
                throw new Exception("???????????????:" + index);
            }
        }
        param.setIndices(indices.toArray(new String[0]));
        // 2.?????????????????????????????????
        List<SearchColumn> columns = Arrays.stream(param.getSearchColumns()).distinct().collect(Collectors.toList());
        if (columns == null || columns.size() == 0) {
            throw new Exception("?????????????????????");
        }
        for (String index : indices) {
            // ????????????????????????
            Set<String> fields = getColumnType(index).keySet();
            // ?????????????????????????????????????????????
            for (SearchColumn sc : columns) {
                if (!fields.contains(sc.getColumn())) {
                    throw new Exception("?????????" + index + "?????????????????????:" + sc.getColumn());
                }
            }
        }
        param.setSearchColumns(columns.toArray(new SearchColumn[0]));

        // ?????????????????????
        SearchSourceBuilder source = new SearchSourceBuilder().query(queryBuilder);
        // 1.????????????
        Highlight hl = param.getHighLight();
        HighlightBuilder hb = new HighlightBuilder();
        for (int i = 0, c = hl.getColumns().length; i < c; i++) {
            // add??????,???update??????
            hb.field(hl.getColumns()[i], hl.getFragmentSize(), hl.getMaxFragmentCount(), hl.getFragmentOffset());
            hb.preTags(hl.getPreTags());
            hb.postTags(hl.getPostTags());
            source.highlighter(hb);
        }
        // 2.?????????????????????????????????
        ColumnFilter columnFilter = param.getColumnFilter();
        source.fetchSource(columnFilter.getIncludes(), columnFilter.getExcludes());
        // 3.??????????????????
        source.from(param.getPage());
        source.size(param.getSize());
        // ??????
        SearchRequest req = new SearchRequest(param.getIndices()).source(source);
        SearchResponse resp = client.search(req, RequestOptions.DEFAULT);
        return resp;
    }

    /**
     * ????????????(???????????????????????????:*)
     * ????????????????????????,?????????
     * @param param ????????????
     * @return
     * @throws IOException
     */
    public String[] wildcardSearch(SearchParam param) throws Exception {
        // ??????????????????
        String searchValue = param.getSearchValue();
        if (!searchValue.startsWith("*")) {
            searchValue = "*" + searchValue;
        }
        if (!searchValue.endsWith("*")) {
            searchValue = searchValue + "*";
        }
        param.setSearchValue(searchValue);
        // ?????????????????????????????????
        Set<String> hits = new LinkedHashSet<>();
        for (SearchColumn sc : Arrays.stream(param.getSearchColumns()).distinct().collect(Collectors.toList())) {
            // ??????????????????????????????
            WildcardQueryBuilder wildcardQuery = QueryBuilders.wildcardQuery(sc.getColumn(), searchValue);
            // ??????
            SearchResponse resp = search(new SearchParam(param.getIndices(), searchValue, sc.getColumn()), wildcardQuery);
            // ????????????
            Iterator<SearchHit> it = resp.getHits().iterator();
            while (it.hasNext()) {
                SearchHit hit = it.next();
                Map<String, Object> source = hit.getSourceAsMap();
                hits.add(source.get(sc.getColumn()).toString());
            }
        }
        return hits.toArray(new String[0]);
    }

    /**
     * ??????es?????????
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        client.close();
    }

    /**
     * ????????????????????????????????????Map(index-(column,highlight))
     * @param hits
     * @return
     */
    public Map<String, Map<String, Set<String>>> getHits(SearchResponse resp) {
        Iterator<SearchHit> it = resp.getHits().iterator();
        Map<String, Map<String, Set<String>>> result = new LinkedHashMap<>();
        while (it.hasNext()) {
            SearchHit hit = it.next();
            String index = hit.getIndex();
            Map<String, Set<String>> fields = result.get(index);
            if (fields == null) {
                fields = new LinkedHashMap<>();
                result.put(index, fields);
            }
            for (HighlightField hf : hit.getHighlightFields().values()) {
                String column = hf.getName();
                Set<String> texts = fields.get(column);
                if (texts == null) {
                    texts = new LinkedHashSet<>();
                    fields.put(column, texts);
                }
                for (Text text : hf.fragments()) {
                    texts.add(text.string());
                }
            }
        }
        return result;
    }


    // -------------------------- ???????????? --------------------------

    /**
     * ???Mapping??????{@link Mapping}??????map,????????????ES???mapping??????
     * @param mappings
     * @return
     */
    private Map<String, Object> buildMappings(Mapping[] mappings) {
        Map<String, Object> properties = new LinkedHashMap<>();
        Map<String, Object> columns = new LinkedHashMap<>();
        properties.put("properties", columns);
        for (int i = 0, c = mappings.length; i < c; i++) {
            Mapping mapping = mappings[i];
            columns.put(mapping.getColumn(), mapping.getProperties());
        }
        return properties;
    }

    /**
     * ???mapping???????????????
     * @param columns
     * @param mapping
     * @param parent
     */
    private void flatColumn(Map<String, Object> columns, Map<String, String> mapping, String parent) {
        parent = Strings.isNull(parent, ".") ? "" : parent;
        Iterator<Entry<String, Object>> it = columns.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> next = it.next();
            String column = Strings.isNull(parent, ".") ? next.getKey() : parent + "." + next.getKey();
            Map<String, Object> value = (Map<String, Object>) next.getValue();
            String type = String.valueOf(value.get("type"));
            // ???????????????????????????????????????(?????????map)
            if (ESType.NESTED.toString().toLowerCase().equals(type)) {
                Map<String, Object> properties = (Map<String, Object>) value.get("properties");
                flatColumn(properties, mapping, column);
            }
            mapping.put(column, type);
        }
    }


    // -------------------------- ????????? --------------------------
    public static class Builder {

        private final String host;
        private final int port;
        private final RestClientBuilder clientBuilder;

        private Builder(String host, int port) {
            this.host = host;
            this.port = port;
            this.clientBuilder = RestClient.builder(new HttpHost(host, port));
        }

        public Builder timeout(int millSeconds) {
            clientBuilder.setRequestConfigCallback(new RequestConfigCallback() {
                @Override
                public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                    return builder.setConnectTimeout(millSeconds).setSocketTimeout(millSeconds);
                }
            });
            return this;
        }

        public Builder password(String user, String pwd) {
            if (Strings.isNotBlank(user)) {
                CredentialsProvider cp = new BasicCredentialsProvider();
                cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
                clientBuilder.setHttpClientConfigCallback(new HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder asyncBuilder) {
                        // ????????????
                        asyncBuilder.setDefaultCredentialsProvider(cp);
                        // ??????????????????
                        asyncBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(4).build());
                        return asyncBuilder;
                    }
                });
            }
            return this;
        }

        public ESClient build() {
            return new ESClient(this);
        }
    }

}
