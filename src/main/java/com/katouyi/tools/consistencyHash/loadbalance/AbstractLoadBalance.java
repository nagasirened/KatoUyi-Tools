package com.katouyi.tools.consistencyHash.loadbalance;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import com.ky.common.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @description: 负载均衡器抽象类
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    abstract ServiceInstance doSelect(LoadBalanceRequest request, List<ServiceInstance> serviceInstanceList);

    @Override
    public ServiceInstance select(LoadBalanceRequest request, List<ServiceInstance> serviceInstanceList) {
        if (null == request || StringUtils.isAnyBlank(request.getKey(), request.getServiceName())
                || CollectionUtils.isEmpty(serviceInstanceList)) {
            return null;
        }

        if (serviceInstanceList.size() == 1) {
            return serviceInstanceList.get(0);
        }

        return doSelect(request, serviceInstanceList);
    }
}
