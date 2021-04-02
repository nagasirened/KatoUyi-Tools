package com.katouyi.tools.elasticSearch2;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
    prefix = "elasticsearch"
)
public class ElasticsearchProperties {
    private Integer port;
    private List<String> servers;

    public ElasticsearchProperties() {
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getServers() {
        return this.servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}