//package com.katouyi.tools.elasticSearch.example;
//
//import com.nagasirened.common.ResponseVO;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
//import org.elasticsearch.action.support.master.AcknowledgedResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
///**
// * author: ZGF
// * context : 索引管理
// */
//@RestController
//@Api(tags = "索引管理")
//public class IndexController {
//
//    @Autowired
//    @Qualifier("restHighLevelClient")
//    private RestHighLevelClient client;
//
//    @PostMapping
//    @ApiOperation("新增索引")
//    public ResponseVO save(String indexName) throws IOException {
//        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
//        client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
//        return ResponseVO.succ();
//    }
//
//    @GetMapping
//    @ApiOperation("查询索引是否存在")
//    public ResponseVO<Boolean> exit(String indexName) throws IOException {
//        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
//        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
//        return ResponseVO.succ(exists);
//    }
//
//    @DeleteMapping
//    @ApiOperation("删除索引 true成功 false失败")
//    public ResponseVO<Boolean> del(String indexName) throws IOException {
//        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
//        AcknowledgedResponse acknowledgedResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
//        return ResponseVO.succ(acknowledgedResponse.isAcknowledged());
//    }
//}
