package com.katouyi.tools.consistencyHash.config;

import com.katouyi.tools.consistencyHash.entity.ServiceInfo;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import org.springframework.util.StringUtils;

import java.util.List;

public interface RegistryClient {

    /**
     * 客户端初始化
     */
    void init();

    /**
     * 根据服务名称获取服务实例信息
     *
     * @param serviceInfo
     * @return
     */
    default List<ServiceInstance> getServices(ServiceInfo serviceInfo) throws Exception  {
        if (null == serviceInfo || StringUtils.isEmpty(serviceInfo.getServiceName())) {
            throw new NullPointerException("service information or service name can`t be null");
        }
        return doGetServices(serviceInfo);
    }

    /**
     * 根据服务名称获取服务实例信息
     *
     * @param serviceInfo
     * @return
     */
    List<ServiceInstance> doGetServices(ServiceInfo serviceInfo) throws Exception ;

    /**
     * 销毁
     */
    void destroy();

}
