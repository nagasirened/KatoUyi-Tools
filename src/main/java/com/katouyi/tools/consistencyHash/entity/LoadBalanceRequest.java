package com.katouyi.tools.consistencyHash.entity;

/**
 * @description: 负载均衡请求POJO
 */
public class LoadBalanceRequest {

    private String key;

    private String serviceName;

    public LoadBalanceRequest(String key, String serviceName) {
        this.key = key;
        this.serviceName = serviceName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
