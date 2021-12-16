package com.katouyi.tools.consistencyHash.loadbalance;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @description: 一致性哈希负载均衡器
 */
@Component
public class ConsistentHashingLoadBalance extends AbstractLoadBalance {
    Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 一致性hash选择器的集合，以服务为纬度
     */
    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    private Lock readLock;
    private Lock writeLock;

    @PostConstruct
    public void initLock() {
        final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        this.readLock = rwLock.readLock();
        this.writeLock = rwLock.writeLock();
    }

    @Override
    ServiceInstance doSelect(LoadBalanceRequest request) {
        readLock.lock();
        try {
            ConsistentHashSelector selector = selectors.get(request.getServiceName());
            if (Objects.isNull(selector)) {
                return null;
            }
            return selector.select(request);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void updateVirtualPoints(String serviceName, List<ServiceInstance> delList, List<ServiceInstance> addList) {
        writeLock.lock();
        try {
            long startTime = System.currentTimeMillis();
            ConsistentHashSelector selector = selectors.get(serviceName);
            if (Objects.isNull(selector)) {
                selectors.put(serviceName, new ConsistentHashSelector(addList));
            } else {
                selector.updateVirtualPoints(delList, addList);
            }
            logger.info("LoadBalance#updateVirtualPoints, update virtual points success, addListSize: {}, delListSize: {}, serviceName: {}, timeCost: {}",
                    addList.size(), delList.size(), serviceName, (System.currentTimeMillis() - startTime));
        } finally {
            writeLock.unlock();
        }
    }

    private static class ConsistentHashSelector {

        /**
         * 虚拟机节点集合
         */
        private final TreeMap<Long, ServiceInstance> virtualInstances;

        /**
         * 物理节点至虚拟节点的复制倍数
         */
        private final int VIRTUAL_COPIES = 16384;

        public ConsistentHashSelector(List<ServiceInstance> virtualInstances) {
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
         * 负载均衡，选择可用节点
         */
        public ServiceInstance select(LoadBalanceRequest request) {
            long hash = hash(request.getKey());
            //所有大于hash的节点
            SortedMap<Long, ServiceInstance> tailMap = virtualInstances.tailMap(hash);
            Long key = tailMap.isEmpty() ? virtualInstances.firstKey() : tailMap.firstKey();
            return virtualInstances.get(key);
        }

        /**
         * 动态更新虚拟节点
         */
        public void updateVirtualPoints(List<ServiceInstance> delList, List<ServiceInstance> addList) {
            delList.forEach(item -> {
                String host = item.getHost();
                for (int i = 0; i < VIRTUAL_COPIES; i++) {
                    this.virtualInstances.remove(hash(host + "#" + i));
                }
            });
            addList.forEach(item -> {
                String host = item.getHost();
                for (int i = 0; i < VIRTUAL_COPIES; i++) {
                    this.virtualInstances.put(hash(host + "#" + i), item);
                }
            });
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
