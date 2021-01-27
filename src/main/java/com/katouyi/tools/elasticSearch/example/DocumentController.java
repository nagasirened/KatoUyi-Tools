//package com.katouyi.tools.elasticSearch.example;
//
//import com.alibaba.fastjson.JSON;
//import com.nagasirened.search.DefaultHignLevelDocumentHandler;
//import com.nagasirened.search.po.Item;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * author: ZGF
// * 11-2020/11/26 : 11:24
// * context :
// */
//@RestController
//@RequestMapping("es")
//public class DocumentController {
//
//    @Autowired
//    private DefaultHignLevelDocumentHandler nagaClient;
//
//    @PostMapping
//    public String add(@RequestBody Item item) throws IOException {
//        nagaClient.save(item, item.getId().toString());
//        return "insert OK";
//    }
//
//    @GetMapping("/info/{indexId}")
//    public String getInfo(@PathVariable String indexId) throws IOException {
//        String detail = nagaClient.detail(indexId);
//        return detail;
//    }
//
//    @PutMapping
//    public String mod(@RequestBody Item item) throws Exception {
//        nagaClient.update(item, item.getId().toString());
//        return "update OK";
//    }
//
//    @DeleteMapping
//    public String del(String itemId) throws IOException {
//        nagaClient.delete(itemId);
//        return "delete OK";
//    }
//
//    /**
//     * 批量操作，  新增、删除和更新
//     * @return
//     */
//    @PostMapping("bulk")
//    public String bulk() throws IOException {
//        BulkRequest bulkRequest = new BulkRequest();
//        bulkRequest.timeout(TimeValue.timeValueSeconds(10));
//        ArrayList<Item> items = new ArrayList<>();
//        items.add(new Item(6L, "狂神说Java", "B站", "爱奇艺", 23D, "b.jpg"));
//        items.add(new Item(7L, "马保国解说日本萌妹子擂台赛", "Bilibili", "腾讯", 44D, "e.jpg"));
//        items.add(new Item(8L, "进击的巨人", "Bilibili", "腾讯", 55D, "g.jpg"));
//
//        for (int i = 0; i < items.size(); i++) {
//            bulkRequest.add(new IndexRequest("naga")
//                    // 不指定ID的话，ID是随机的
//                    .id(items.get(i).getId().toString())
//                    .source(JSON.toJSONString(items.get(i)), XContentType.JSON)
//            );
//            // bulkRequest.add(UpdateRequest)   批量更新
//            // bulkRequest.add(DeleteRequest)   批量删除
//        }
//
//        // BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//        return "bulk insert OK";
//    }
//
//
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;
//
//    /**
//     * 分页，条件查询
//     * @param brandName
//     * @return
//     */
//    @PostMapping("/get")
//    public String get(String brandName, int from, int size) throws IOException {
//        // 条件构造器
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        // 查询构建器 QueryBuilders工具类可以快速构建
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("brand", brandName);
//        builder.query(matchQueryBuilder);
//        builder.timeout(TimeValue.timeValueSeconds(10));
//        /** 分页查询 */
//        builder.from(from);
//        builder.size(size);
//
//        SearchRequest searchRequest = new SearchRequest("naga");
//        searchRequest.source(builder);
//
//        SearchResponse result = client.search(searchRequest, RequestOptions.DEFAULT);
//        for (SearchHit temp : result.getHits().getHits()) {
//            System.out.println(temp.getSourceAsMap());
//        }
//        return "query OK";
//    }
//
//    /**
//     * 查询价格在20以上的
//     */
//    @PostMapping("/range")
//    public String query() throws IOException {
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                .filter(new RangeQueryBuilder("price").gte(20.0D));
//        sourceBuilder.query(queryBuilder);
//
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.source(sourceBuilder);
//
//        SearchResponse result = client.search(searchRequest, RequestOptions.DEFAULT);
//        for (SearchHit temp : result.getHits().getHits()) {
//            System.out.println(temp.getSourceAsMap());
//        }
//        return "query OK";
//    }
//}
