package com.katouyi.tools.consistencyHash.entity;

/**
 * @description: K8s服务实例信息POJO
 */
public class K8sServiceInstance implements ServiceInstance {

    private String namespace;

    private String serviceName;

    private String host;

    private int port;

    public K8sServiceInstance(String namespace, String serviceName, String host, int port) {
        this.namespace = namespace;
        this.serviceName = serviceName;
        this.host = host;
        this.port = port;
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

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
