package com.katouyi.tools.elasticSearch2;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ElasticsearchClient elasticsearchClient;
    private BulkProcessor bulkProcessor;

    public DocumentServiceImpl() {
    }

    @PostConstruct
    public void init() {
        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer = (request, listener) -> {
            this.elasticsearchClient.getClient().bulkAsync(request, RequestOptions.DEFAULT, listener);
        };
        this.bulkProcessor = BulkProcessor.builder(bulkConsumer, new Listener() {
            public void beforeBulk(long executionId, BulkRequest request) {
                DocumentServiceImpl.this.logger.debug("elasticsearch service,document service,batch save start,data size:{}", request.numberOfActions());
            }

            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                DocumentServiceImpl.this.logger.info("elasticsearch service,document service,batch save finished,data size:{},tookInMillis:{},fail message:{}", new Object[]{request.numberOfActions(), response.getIngestTookInMillis(), response.buildFailureMessage()});
            }

            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                DocumentServiceImpl.this.logger.error("elasticsearch service,document service,batch save fail,error message:{},data:{}", new Object[]{failure.getMessage(), JSON.toJSONString(request.requests()), failure});
            }
        }).setBulkActions(1000).setBulkSize(new ByteSizeValue(5L, ByteSizeUnit.MB)).setFlushInterval(TimeValue.timeValueSeconds(5L)).setConcurrentRequests(2).setFlushInterval(TimeValue.timeValueSeconds(5L)).setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3)).build();
    }

    @PreDestroy
    public void destroy() {
        try {
            this.logger.error("elasticsearch service,document service,destroy application,batch save document");
            this.bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);
            this.logger.error("elasticsearch service,document service,destroy application,batch save document successful");
        } catch (Exception var2) {
            this.logger.error("elasticsearch service,document service,destroy application,batch save document fail,error message:{}", var2.getMessage(), var2);
        }

    }

    public RestHighLevelClient getRestHighLevelClient() {
        return this.elasticsearchClient.getClient();
    }

    public void save(String indexName, DocumentEntity entity) {
        try {
            IndexRequest request = new IndexRequest(indexName);
            request.id(entity.getId());
            request.source(entity);
            this.bulkProcessor.add(request);
            this.logger.debug("elasticsearch service,document service,batch save successful,indexName:{},data size:{}", indexName, JSON.toJSONString(entity));
        } catch (Exception var4) {
            this.logger.error("elasticsearch service,document service,batch save fail,indexName:{},data:{},error message:{}", new Object[]{indexName, JSON.toJSONString(entity), var4.getMessage(), var4});
        }

    }

    public void batchSave(String indexName, List<DocumentEntity> dataList) {
        try {
            for(int i = 0; i < dataList.size(); ++i) {
                DocumentEntity entity = (DocumentEntity)dataList.get(i);
                IndexRequest request = new IndexRequest(indexName);
                request.id(entity.getId());
                request.source(entity);
                this.bulkProcessor.add(request);
            }

            this.logger.debug("elasticsearch service,document service,batch save successful,indexName:{},data size:{}", indexName, dataList.size());
        } catch (Exception var6) {
            this.logger.error("elasticsearch service,document service,batch save fail,indexName:{},data:{},error message:{}", new Object[]{indexName, JSON.toJSONString(dataList), var6.getMessage(), var6});
        }

    }

    public Map<String, Object> get(String indexName, String id) {
        try {
            GetRequest request = new GetRequest(indexName);
            request.id(id);
            GetResponse response = this.elasticsearchClient.getClient().get(request, RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (Exception var5) {
            this.logger.error("document service,delete fail,indexName:{},id:{},data:{},error message:{}", new Object[]{indexName, id, var5.getMessage(), var5});
            return Collections.emptyMap();
        }
    }

    public Pager<Map<String, Object>> find(SearchRequest request) {
        Pager<Map<String, Object>> pager = new Pager();
        pager.setPageSize(request.source().size());

        try {
            long currentTime = System.currentTimeMillis();
            SearchResponse response = this.elasticsearchClient.getClient().search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            long costTime = System.currentTimeMillis() - currentTime;
            if (costTime > 50L && this.logger.isInfoEnabled()) {
                this.logger.warn("document service,find successful,indexName:{},source:{},total:{},took time:{},cost time:{}", new Object[]{request.indices(), request.source(), hits.getTotalHits().value, response.getTook().getStringRep(), costTime});
            }

            pager.setTotalCount(hits.getTotalHits().value);
            SearchHit[] searchHitArray = hits.getHits();
            List<Map<String, Object>> data = new ArrayList(searchHitArray.length);
            SearchHit[] var11 = searchHitArray;
            int var12 = searchHitArray.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                SearchHit hit = var11[var13];
                Map<String, Object> source = hit.getSourceAsMap();
                source.put("score", hit.getScore());
                HashMap<String, String> highlightFields = new HashMap();
                Set<String> set = hit.getHighlightFields().keySet();
                Iterator var18 = set.iterator();

                while(var18.hasNext()) {
                    String str = (String)var18.next();
                    HighlightField highLight = (HighlightField)hit.getHighlightFields().get(str);
                    String name = highLight.getName();
                    Text[] texts = highLight.getFragments();
                    if (texts.length > 1) {
                        this.logger.warn("document service,search,indexName:{},searchText:{},source:{},texts:{}", new Object[]{request.indices(), request.source(), JSON.toJSONString(source), JSON.toJSONString(texts)});
                    }

                    for(int i = 0; i < texts.length; ++i) {
                        Text text = texts[i];
                        highlightFields.put(name, text.string());
                    }
                }

                if (MapUtils.isEmpty(highlightFields)) {
                    data.add(source);
                } else {
                    source.put("highlightFields", highlightFields);
                    data.add(source);
                }
            }

            if (CollectionUtils.isEmpty(data)) {
                this.logger.warn("document service,find fail,indexName:{},request source:{}", request.indices(), request.source().toString());
            }

            pager.setList(data);
            return pager;
        } catch (Exception var25) {
            this.logger.error("document service,find fail,indexName:{},source:{},error message:{}", new Object[]{request.indices(), request.source(), var25.getMessage(), var25});
            return pager;
        }
    }

    public void delete(String indexName, String id) {
        try {
            DeleteRequest request = new DeleteRequest(indexName);
            request.id(id);
            this.elasticsearchClient.getClient().delete(request, RequestOptions.DEFAULT);
        } catch (Exception var4) {
            this.logger.error("document service,delete fail,indexName:{},id:{},data:{},error message:{}", new Object[]{indexName, id, var4.getMessage(), var4});
        }

    }

    public long count(String indexName) {
        try {
            CountRequest request = new CountRequest();
            request.indices(new String[]{indexName});
            CountResponse response = this.elasticsearchClient.getClient().count(request, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (Exception var4) {
            this.logger.error("document service,count fail,indexName:{},error message:{}", new Object[]{indexName, var4.getMessage(), var4});
            return 0L;
        }
    }
}