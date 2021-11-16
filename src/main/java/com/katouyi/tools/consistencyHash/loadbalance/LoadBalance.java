package com.katouyi.tools.consistencyHash.loadbalance;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;

import java.util.List;

/**
 * @description: 负载均衡器接口
 */
public interface LoadBalance {

    /**
     * 负载均衡，公共方法，判断非空
     *
     * @param request
     * @param serviceInstanceList
     * @return
     */
    ServiceInstance select(LoadBalanceRequest request, List<ServiceInstance> serviceInstanceList);

}
