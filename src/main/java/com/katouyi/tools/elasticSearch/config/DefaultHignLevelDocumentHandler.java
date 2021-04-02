package com.katouyi.tools.elasticSearch.config;

import com.alibaba.fastjson.JSON;
import com.katouyi.tools.elasticSearch2.DocumentEntity;
import com.katouyi.tools.elasticSearch2.DocumentServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * author: ZGF
 * context : 默认
 */
@Slf4j
public class DefaultHignLevelDocumentHandler<T, ID> implements HignLevelDocumentHandler<T, ID> {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Getter
    @Setter
    private String indexName;

    private BulkProcessor bulkProcessor;

    public DefaultHignLevelDocumentHandler(String indexName){
        this.indexName = indexName;
    }

    @PostConstruct
    public void init(){
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = (request, listener) -> {
            client.bulkAsync(request, RequestOptions.DEFAULT, listener);
        };
        this.bulkProcessor = BulkProcessor.builder(bulkConsumer, new BulkProcessor.Listener() {
            public void beforeBulk(long executionId, BulkRequest request) {
                log.debug("elasticsearch service,document service,batch save start,data size:{}", request.numberOfActions());
            }

            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                log.info("elasticsearch service,document service,batch save finished,data size:{},tookInMillis:{},fail message:{}", new Object[]{request.numberOfActions(), response.getIngestTookInMillis(), response.buildFailureMessage()});
            }

            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                log.error("elasticsearch service,document service,batch save fail,error message:{},data:{}", new Object[]{failure.getMessage(), JSON.toJSONString(request.requests()), failure});
            }
        }).setBulkActions(1000)         // 数量达到1000
        .setBulkSize(new ByteSizeValue(5L, ByteSizeUnit.MB))        // 达到5M
        .setFlushInterval(TimeValue.timeValueSeconds(5L))                // 达到5s
        .setConcurrentRequests(2)                                        // 请求数量达到2
        .setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3))  //重试策略
        .build();

    }

    @Override
    public void save(T t) throws Exception {
        if (judgeId(t)) {
            Field id = t.getClass().getDeclaredField("id");
            save(t, id.toString());
            return;
        }
        save(t, null);
    }

    public void save(T t, String id) throws IOException {
        jsonSave(t, id, TimeValue.timeValueSeconds(1));
    }

    public void saveAll(List<DocumentEntity> datas){
        try {
            datas.forEach(item -> {
                DocumentEntity entity = (DocumentEntity) item;
                IndexRequest request = new IndexRequest(indexName);
                request.id(entity.getId());
                request.source(entity);
                this.bulkProcessor.add(request);
            });
            log.debug("elasticsearch service,document service,batch save successful,indexName:{},data size:{}", indexName, datas.size());
        } catch (Exception var6) {
            log.error("elasticsearch service,document service,batch save fail,indexName:{},data:{},error message:{}", new Object[]{indexName, JSON.toJSONString(datas), var6.getMessage(), var6});
        }
    }

    public void jsonSave(T t, String id, TimeValue timeValue) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(id);
        indexRequest.timeout(timeValue);
        indexRequest.source(JSON.toJSONString(t), XContentType.JSON);
        // client.index(indexRequest, RequestOptions.DEFAULT);
        bulkProcessor.add(indexRequest);
    }

    @Override
    public String detail(ID id) throws IOException {
        GetRequest request = new GetRequest(indexName, id.toString());
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        return getResponse.getSourceAsString();
    }

    @Override
    public void update(T t) throws Exception {
        if (judgeId(t)) {
            Field id = t.getClass().getDeclaredField("id");
            save(t, id.toString());
        } else {
            update(t, null);
        }
    }

    public void update(T t, String id) throws Exception {
        update(t, id, TimeValue.timeValueSeconds(1));
    }

    public void update(T t, String id, TimeValue timeValue) throws Exception {
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        updateRequest.timeout(timeValue);
        updateRequest.doc(JSON.toJSONString(t), XContentType.JSON);
        client.update(updateRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void delete(ID id) throws IOException {
        delete(id, TimeValue.timeValueSeconds(1));
    }

    public void delete(ID id, String timeout) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.timeout(timeout);
        deleteRequest.id(id.toString());
        client.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public void delete(ID id, TimeValue timeValue) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.timeout(timeValue);
        deleteRequest.id(id.toString());
        client.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void bulk(BulkRequest bulkRequest) throws IOException {
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public SearchResponse search(SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    private boolean judgeId(T t){
        //获取这个类的所有属性
        Field[] fields = t.getClass().getDeclaredFields();
        boolean flag = false;
        //循环遍历所有的fields
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals("id")) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
