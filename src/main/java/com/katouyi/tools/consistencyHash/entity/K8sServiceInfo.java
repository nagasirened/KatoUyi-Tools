package com.katouyi.tools.consistencyHash.entity;

/**
 * @description: K8s服务信息POJO
 */
public class K8sServiceInfo implements ServiceInfo {

    private String namespace;

    private String serviceName;

    public K8sServiceInfo(String namespace, String serviceName) {
        this.namespace = namespace;
        this.serviceName = serviceName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
