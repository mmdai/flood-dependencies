package cn.flood.elasticsearch.client;

import cn.flood.elasticsearch.page.Page;
import cn.flood.elasticsearch.constant.ElasticSearchConstant;
import cn.flood.json.JsonUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

/**
 * ES REST 客户端
 *
 */
public class FloodRestHighLevelClient {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public UpdateResponse update(UpdateRequest updateRequest) throws IOException {
        return this.doUpdate(updateRequest, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(UpdateRequest updateRequest, RequestOptions options) throws IOException {
        return this.doUpdate(updateRequest, options);
    }

    public <T> UpdateResponse update(String index, String type, String id, T entity) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJSONString(entity), XContentType.JSON);
        return update(updateRequest, RequestOptions.DEFAULT);
    }

    public <T> UpdateResponse update(String index, String type, String id, T entity, RequestOptions options) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJSONString(entity), XContentType.JSON);
        return update(updateRequest, options);
    }

    public UpdateResponse update(String index, String type, String id, Map<String, Object> document) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJSONString(document), XContentType.JSON);
        return update(updateRequest, RequestOptions.DEFAULT);
    }

    public UpdateResponse update(String index, String type, String id, Map<String, Object> document, RequestOptions options) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, type, id);
        updateRequest.doc(JsonUtils.toJSONString(document), XContentType.JSON);
        return update(updateRequest, options);
    }

    /**
     * 适用于简单字段更新
     * @param index
     * @param query
     * @param document
     * @return
     */
    public BulkByScrollResponse updateByQuery(String index, QueryBuilder query, Map<String, Object> document) throws IOException {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
        updateByQueryRequest.setQuery(query);

        StringBuilder script = new StringBuilder();
        Set<String> keys = document.keySet();
        for (String key : keys) {
            String appendValue = "";
            Object value = document.get(key);
            if (value instanceof Number) {
                appendValue = value.toString();
            } else if (value instanceof String) {
                appendValue = "'" + value.toString() + "'";
            } else if (value instanceof List){
                appendValue = JsonUtils.toJSONString(value);
            } else {
                appendValue = value.toString();
            }
            script.append("ctx._source.").append(key).append("=").append(appendValue).append(";");
        }
        updateByQueryRequest.setScript(new Script(script.toString()));
        return updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
    }

    public BulkByScrollResponse updateByQuery(UpdateByQueryRequest updateByQueryRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.UPDATE_BY_QUERY_REQUEST, updateByQueryRequest.toString());
        return restHighLevelClient.updateByQuery(updateByQueryRequest, options);
    }

    private UpdateResponse doUpdate(UpdateRequest updateRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.UPDATE_REQUEST, updateRequest.toString());
        return restHighLevelClient.update(updateRequest, options);
    }

    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        return restHighLevelClient.search(searchRequest, options);
    }

    public <T> Page<T> searchByPage(SearchRequest searchRequest, Class<T> entityClass) throws IOException {
        return searchByPage(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public <T> Page<T> searchByPage(int page, int pageSize, SearchRequest searchRequest, Class<T> entityClass) throws IOException {
        searchRequest.source().from(Page.page2Start(page, pageSize)).size(pageSize);
        return searchByPage(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public <T> Page<T> searchByPage(SearchRequest searchRequest, Class<T> entityClass, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
        long totalHits = searchResponse.getHits().getTotalHits().value;
        List<T> datas = buildSearchResult(searchResponse, entityClass);
        SearchSourceBuilder searchSourceBuilder = searchRequest.source();
        return new Page(searchSourceBuilder.from(), searchSourceBuilder.size(), datas, totalHits);
    }

    public <T> List<T> search(SearchRequest searchRequest, Class<T> entityClass, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.SEARCH_REQUEST, searchRequest.toString());
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
        List<T> datas = buildSearchResult(searchResponse, entityClass);
        return datas;
    }

    private <T> List<T> buildSearchResult(SearchResponse searchResponse, Class<T> entityClass) {
        List<T> datas = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            datas.add(JsonUtils.toJavaObject(source, entityClass));
        }
        return datas;
    }

    public <T> List<T> search(SearchRequest searchRequest, Class<T> entityClass) throws IOException {
        return search(searchRequest, entityClass, RequestOptions.DEFAULT);
    }

    public IndexResponse index(IndexRequest indexRequest) throws IOException {
        return index(indexRequest, RequestOptions.DEFAULT);
    }

    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.INDEX_REQUEST, indexRequest.toString());
        return restHighLevelClient.index(indexRequest, options);
    }

    public <T> IndexResponse index(String index, String type, T entity) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JsonUtils.toJSONString(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, RequestOptions options) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(JsonUtils.toJSONString(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, options);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtils.toJSONString(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    public <T> IndexResponse index(String index, String type, T entity, String id, RequestOptions options) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JsonUtils.toJSONString(entity), XContentType.JSON);
        IndexResponse indexResponse = index(indexRequest, options);
        return indexResponse;
    }

    public DeleteResponse delete(DeleteRequest deleteRequest) throws IOException {
       return delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.DELETE_REQUEST, deleteRequest.toString());
        return restHighLevelClient.delete(deleteRequest, options);
    }

    public DeleteResponse delete(String index, String type, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        return delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public DeleteResponse delete(String index, String type, String id, RequestOptions options) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        return delete(deleteRequest, options);
    }

    public CountResponse count(CountRequest countRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.COUNT_REQUEST, countRequest.toString());
        return restHighLevelClient.count(countRequest, options);
    }

    public CountResponse count(CountRequest countRequest) throws IOException {
       return count(countRequest, RequestOptions.DEFAULT);
    }

    public long countResult(CountRequest countRequest) throws IOException {
        return count(countRequest, RequestOptions.DEFAULT).getCount();
    }

    public long countResult(CountRequest countRequest, RequestOptions options) throws IOException {
        return count(countRequest, options).getCount();
    }

    public boolean existsSource(GetRequest getRequest, RequestOptions options) throws IOException {
        Map<String, Object> catData = new HashMap<>(1);
        catData.put(ElasticSearchConstant.EXISTS_SOURCE_REQUEST, getRequest.toString());
        return restHighLevelClient.existsSource(getRequest, options);
    }

    public boolean existsSource(GetRequest getRequest) throws IOException {
       return existsSource(getRequest, RequestOptions.DEFAULT);
    }
}