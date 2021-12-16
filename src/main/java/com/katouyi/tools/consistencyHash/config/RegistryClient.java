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
    void loadServices(ServiceInfo serviceInfo, final boolean asyncMark);

    /**
     * 销毁
     */
    void destroy();

}
