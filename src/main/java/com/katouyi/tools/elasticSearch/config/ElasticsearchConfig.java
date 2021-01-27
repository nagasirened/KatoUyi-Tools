package com.katouyi.tools.elasticSearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author: ZGF
 * context : ES 配置类
 */

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                        // 如果是集群，可以构建多个
                        /*,new HttpHost("localhost", 9201, "http")*/
                )
        );

        return restHighLevelClient;
    }

    /**
     * 注意，这里的 Object 建议改成对应的
     * @return
     */
    @Bean(name = "nagaClient")
    @ConditionalOnMissingBean(name = "nagaClient")
    public DefaultHignLevelDocumentHandler defaultHignLevelDocumentHandler(){
        DefaultHignLevelDocumentHandler< /** 对应的存储es的类，以及主键 */Object, Long> handler =
                new DefaultHignLevelDocumentHandler<>(/** indexName */"naga");
        return handler;
    }

}
