package com.katouyi.tools.elasticSearch2;

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchClient {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private RestHighLevelClient client;
    Sniffer sniffer;
    @Resource
    ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchClient() {
    }

    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(this.elasticsearchProperties.getServers())) {
            this.logger.error("elasticsearch client init fail,server host list is empty");
        } else {
            List<HttpHost> httpHosts = new ArrayList();
            Iterator var2 = this.elasticsearchProperties.getServers().iterator();

            while(var2.hasNext()) {
                String server = (String)var2.next();
                httpHosts.add(new HttpHost(server, this.elasticsearchProperties.getPort(), "http"));
            }

            if (CollectionUtils.isEmpty(httpHosts)) {
                this.logger.error("elasticsearch client init fail,server host list is empty");
            } else {
                RestClientBuilder builder = RestClient.builder((HttpHost[])httpHosts.toArray(new HttpHost[httpHosts.size()]));
                this.client = new RestHighLevelClient(builder);
                this.sniffer = Sniffer.builder(builder.build()).setSniffIntervalMillis(60000).build();
                this.logger.info("elasticsearch client init successful,hosts:{}", JSON.toJSON(httpHosts));
            }
        }
    }

    public RestHighLevelClient getClient() {
        return this.client;
    }

    @PreDestroy
    public void close() {
        try {
            this.sniffer.close();
            this.client.close();
        } catch (Exception var2) {
            this.logger.error("elasticsearch client close fail,error message:{}", var2.getMessage(), var2);
        }

    }
}
