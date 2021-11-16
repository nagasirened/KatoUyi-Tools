package com.katouyi.tools.consistencyHash.loadbalance;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 一致性哈希负载均衡器
 */
@Component
public class ConsistentHashingLoadBalance extends AbstractLoadBalance {
    /**
     * 一致性hash选择器的集合，以服务为纬度
     */
    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    ServiceInstance doSelect(LoadBalanceRequest request, List<ServiceInstance> serviceInstanceList) {
        int invokersHashCode = serviceInstanceList.hashCode();
        ConsistentHashSelector selector = selectors.get(request.getServiceName());
        if (selector == null || selector.identityHashCode != invokersHashCode) {
            selectors.put(request.getServiceName(), new ConsistentHashSelector(serviceInstanceList, invokersHashCode));
            selector = selectors.get(request.getServiceName());
        }
        ServiceInstance select = selector.select(request);
        return select;
    }

    private static class ConsistentHashSelector {

        /**
         * 虚拟机节点集合
         */
        private final TreeMap<Long, ServiceInstance> virtualInstances;

        /**
         * 服务列表HashCode
         */
        private final int identityHashCode;

        /**
         * 物理节点至虚拟节点的复制倍数
         */
        private final int VIRTUAL_COPIES = 160;

        public ConsistentHashSelector(List<ServiceInstance> virtualInstances, int identityHashCode) {
            this.identityHashCode = identityHashCode;

            //添加物理节点
            TreeMap<Long, ServiceInstance> treeMap = new TreeMap<>();
            for (ServiceInstance virtualInstance : virtualInstances) {
                for (int idx = 0; idx < VIRTUAL_COPIES; ++idx) {
                    long hash = hash(virtualInstance.getHost() + "#" + idx);
                    treeMap.put(hash, virtualInstance);
                }
            }
            this.virtualInstances = treeMap;
        }

        /**
         * 负载均衡
         *
         * @param request
         * @return
         */
        public ServiceInstance select(LoadBalanceRequest request) {
            long hash = hash(request.getKey());
            //所有大于hash的节点
            SortedMap<Long, ServiceInstance> tailMap = virtualInstances.tailMap(hash);
            Long key = tailMap.isEmpty() ? virtualInstances.firstKey() : tailMap.firstKey();
            return virtualInstances.get(key);
        }

        /**
         * 计算hash值
         *
         * @param key
         * @return
         */
        private static Long hash(String key) {
            final int p = 16777619;
            Long hash = 2166136261L;
            for (int idx = 0, num = key.length(); idx < num; ++idx) {
                hash = (hash ^ key.charAt(idx)) * p;
            }
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;

            if (hash < 0) {
                hash = Math.abs(hash);
            }
            return hash;
        }
    }
}
