package com.katouyi.tools.elasticSearch2;

import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;

public interface DocumentService {
    RestHighLevelClient getRestHighLevelClient();

    void save(String indexName, DocumentEntity entity);

    void batchSave(String indexName, List<DocumentEntity> dataList);

    Map<String, Object> get(String indexName, String id);

    Pager<Map<String, Object>> find(SearchRequest request);

    void delete(String indexName, String id);

    long count(String indexName);
}
