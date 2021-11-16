package com.katouyi.tools.consistencyHash.entity;

/**
 * @description: 服务实例信息接口POJO
 */
public interface ServiceInstance {

    /**
     * 获取服务名称
     *
     * @return
     */
    String getServiceName();

    /**
     * 获取IP
     *
     * @return
     */
    String getHost();

    /**
     * 获取端口
     *
     * @return
     */
    int getPort();

}
