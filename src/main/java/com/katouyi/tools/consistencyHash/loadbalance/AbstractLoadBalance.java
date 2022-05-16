package com.katouyi.tools.consistencyHash.loadbalance;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import org.apache.commons.lang3.StringUtils;

/**
 * @description: 负载均衡器抽象类
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public ServiceInstance select(LoadBalanceRequest request) {
        if (null == request || StringUtils.isAnyBlank(request.getKey(), request.getServiceName())) {
            return null;
        }

        return doSelect(request);
    }

    abstract ServiceInstance doSelect(LoadBalanceRequest request);
}
