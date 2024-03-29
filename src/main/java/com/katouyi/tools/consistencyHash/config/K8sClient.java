package com.katouyi.tools.consistencyHash.config;

import com.alibaba.fastjson.JSON;
import com.katouyi.tools.consistencyHash.entity.K8sServiceInfo;
import com.katouyi.tools.consistencyHash.entity.K8sServiceInstance;
import com.katouyi.tools.consistencyHash.entity.ServiceInfo;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import com.katouyi.tools.consistencyHash.loadbalance.LoadBalance;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class K8sClient implements RegistryClient {

    private final Logger logger = LoggerFactory.getLogger(K8sClient.class);
    public static final String SERVICE_NAME = "service-name-***";
    public static final String NAMESPACE = "namespace***";

    /**
     * 初始化标志
     */
    private final AtomicBoolean init = new AtomicBoolean();

    /**
     * 销毁标志
     */
    private final AtomicBoolean destroy = new AtomicBoolean();

    /**
     * k8s客户端
     */
    private volatile KubernetesClient client = null;

    /**
     * 服务监听缓存，需要以服务（唯一字符串）纬度来加锁
     */
    private final Map<String, Watch> serviceWatchMap = new ConcurrentHashMap<>();

    /**
     * 服务列表缓存，需要serviceLockMap来加锁
     */
    private final Map<String, List<ServiceInstance>> serviceMap = new ConcurrentHashMap<>();


    public String getMasterUrl() {
        return "https://xxx.xxx.xxx.xxx:6443";
    }

    @javax.annotation.Resource(name = "consistentHashingLoadBalance")
    private LoadBalance loadBalance;

    /**
     * 初始化客户端
     */
    @Override
    @PostConstruct
    public void init() {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        if (null == client) {
            String masterUrl = getMasterUrl();
            if (StringUtils.isBlank(masterUrl)) {
                logger.warn("K8sClient#init, k8s dove master url is null, check config!");
            }
            Config config = new ConfigBuilder().withMasterUrl(masterUrl).build();
            client = new DefaultKubernetesClient(config);
            logger.info("K8sClient#init, load KubernetesClient success");
        }
        loadServices(new K8sServiceInfo(NAMESPACE, SERVICE_NAME), false);
    }

    /**
     * 获取服务列表
     *
     * @param serviceInfo   k8s参数
     * @return              服务列表
     */
    @Override
    public void loadServices(ServiceInfo serviceInfo, final boolean asyncMark) {
        if (Objects.isNull(client)) {
            return;
        }

        K8sServiceInfo k8sServiceInfo = (K8sServiceInfo) serviceInfo;
        if (StringUtils.isEmpty(k8sServiceInfo.getNamespace())) {
            logger.error("K8sClient, k8sServiceInfo namespace is null, please check.");
            return;
        }

        //服务请求名称
        String nameServiceName = packageServiceName(k8sServiceInfo);
        //如果不存在Watch，初始化服务，以服务纬度加锁
        if (!serviceWatchMap.containsKey(nameServiceName) || Objects.isNull(serviceWatchMap.get(nameServiceName))) {
            if (!asyncMark) {
                initService(nameServiceName, k8sServiceInfo, asyncMark);
            } else {
                new Thread(() -> initService(nameServiceName, k8sServiceInfo, asyncMark)).start();
            }
        }
    }

    /**
     * 获取服务名称的唯一引用
     */
    private String packageServiceName(K8sServiceInfo k8sServiceInfo) {
        String serviceName = k8sServiceInfo.getNamespace() + "-" + k8sServiceInfo.getServiceName();
        return serviceName.intern();
    }

    /**
     * 初始化服务
     */
    public void initService(String nameServiceName, K8sServiceInfo k8sServiceInfo, boolean asyncMark) {
        // 在此判断，防止重复加载
        if (serviceWatchMap.containsKey(nameServiceName) && Objects.nonNull(serviceWatchMap.get(nameServiceName))) {
            return;
        }
        Resource<Endpoints> resource;
        try {
            //链接指定的Namespace的Service
            resource = client.endpoints().inNamespace(k8sServiceInfo.getNamespace()).withName(k8sServiceInfo.getServiceName());
        } catch (Exception e) {
            logger.error("K8sClient#initService, get service list fail, info: {}", JSON.toJSONString(k8sServiceInfo));
            return;
        }

        try {
            if (null == resource || null == resource.get()) {
                logger.error("K8sClient#initService, service resource not found, namespace: {}, serviceName: {}", k8sServiceInfo.getNamespace(), k8sServiceInfo.getServiceName());
                return;
            }
        } catch (Exception e) {
            logger.info("K8sClient#initService, resource.get() error!");
            return;
        }

        //如果监听为空，添加监听
        if (Objects.isNull(serviceWatchMap.get(nameServiceName))) {
            serviceWatchMap.put(nameServiceName, resource.watch(new Watcher<Endpoints>() {
                @Override
                public void eventReceived(Action action, Endpoints endpoints) {
                    logger.info("K8sClient#initService, service list has changed. k8sInfo: {}, actionName: {}, endpoints: {}, asyncMark: {}", JSON.toJSONString(k8sServiceInfo), action.name(), endpoints, asyncMark);
                    switch (action) {
                        case ADDED:
                        case DELETED:
                        case ERROR:
                            break;
                        case MODIFIED:
                            modifyServiceInstance(nameServiceName, k8sServiceInfo.getServiceName(), endpoints);
                            break;
                        default:
                            logger.error("K8sClient#initService, without this type of change. k8sInfo: {}, actionName: {}, endpoints: {}", JSON.toJSONString(k8sServiceInfo), action.name(), endpoints);
                            break;
                    }
                }

                /**
                 * 异常关闭，重新注册一下监听器
                 */
                @Override
                public void onClose(WatcherException e) {
                    logger.error("k8s registry client is close! we will Re-register the listener", e);
                    restart(nameServiceName, k8sServiceInfo);
                }

                @Override
                public void onClose() {
                    logger.error("k8s registry client is close! we will Re-register the listener");
                    restart(nameServiceName, k8sServiceInfo);
                }
            }));
        } else {
            logger.info("service[{}] watch is exist, don`t need add watch", nameServiceName);
        }
        //更新服务列表缓存
        modifyServiceInstance(nameServiceName, k8sServiceInfo.getServiceName(), resource.get());
    }

    /**
     * 意外退出重启
     */
    private void restart(String nameServiceName, K8sServiceInfo k8sServiceInfo) {
        Watch watch = serviceWatchMap.get(nameServiceName);
        if (Objects.nonNull(watch)) {
            watch.close();
        }
        serviceWatchMap.clear();
        loadServices(k8sServiceInfo, true);
    }

    /**
     * 更新服务列表缓存
     *
     * @param serviceName
     * @param endpoints
     * @return
     */
    private void modifyServiceInstance(String nameServiceName, String serviceName, Endpoints endpoints) {
        long startTime = System.currentTimeMillis();
        List<ServiceInstance> serviceInstances = new ArrayList<>();
        if (!CollectionUtils.isEmpty(endpoints.getSubsets())) {
            //命名空间和服务名称
            String namespace = endpoints.getMetadata().getNamespace();
            String name = endpoints.getMetadata().getName();

            //遍历所有的endpoint，取出IP地址和端口，构建成Server实例，放入result集合中
            for (EndpointSubset subset : endpoints.getSubsets()) {
                serviceInstances.addAll(packServiceInstance(subset, namespace, name));
            }
        } else {
            logger.info("K8sClient#modifyServiceInstance, serviceName {} instance modify, empty!", nameServiceName);
        }
        // 增量替换
        List<ServiceInstance> delList = new ArrayList<>();
        List<ServiceInstance> addList = new ArrayList<>();
        dealUpdatedServiceInstance(delList, addList, nameServiceName, serviceInstances);
        if (!CollectionUtils.isEmpty(delList) || !CollectionUtils.isEmpty(addList)) {
            loadBalance.updateVirtualPoints(serviceName, delList, addList);
        }
        logger.info("K8sClient#modifyServiceInstance and updateVirtualPoints useful serviceInstance list size: {}, subsetSize: {}, serviceName: {}, timeCost: {}, detail: {}",
                serviceInstances.size(), endpoints.getSubsets().size(), nameServiceName, (System.currentTimeMillis() - startTime), JSON.toJSONString(serviceInstances));
        serviceMap.put(nameServiceName, serviceInstances);
    }

    /**
     * 节点差集
     */
    public void dealUpdatedServiceInstance(List<ServiceInstance> delList, List<ServiceInstance> addList, String nameServiceName, List<ServiceInstance> realTimeServiceInstance) {
        List<ServiceInstance> oldServiceInstances = serviceMap.get(nameServiceName);
        if (CollectionUtils.isEmpty(oldServiceInstances)) {
            addList.addAll(realTimeServiceInstance);
            return;
        }
        Set<String> oldHostPortSet = oldServiceInstances.stream().map(item -> item.getHost() + ":" + item.getPort()).collect(Collectors.toSet());
        addList.addAll(realTimeServiceInstance.stream().filter(item -> !oldHostPortSet.contains(item.getHost() + ":" + item.getPort())).collect(Collectors.toList()));

        Set<String> realTimeHostPortSet = realTimeServiceInstance.stream().map(item -> item.getHost() + ":" + item.getPort()).collect(Collectors.toSet());
        delList.addAll(oldServiceInstances.stream().filter(item -> !realTimeHostPortSet.contains(item.getHost() + ":" + item.getPort())).collect(Collectors.toList()));
    }

    /**
     * 包装服务实例信息
     *
     * @param subset
     * @param serviceName
     * @return
     */
    public List<ServiceInstance> packServiceInstance(EndpointSubset subset, String namespace, String serviceName) {
        List<ServiceInstance> instances = new ArrayList<>();
        //目前仅支持单一port，多个IP
        if (subset.getPorts().size() == 1) {
            EndpointPort port = subset.getPorts().get(0);
            List<EndpointAddress> notReadyAddresses = subset.getNotReadyAddresses();
            Set<String> notReadyIpSet = notReadyAddresses.stream().map(EndpointAddress::getIp).collect(Collectors.toSet());
            for (EndpointAddress address : subset.getAddresses()) {
                if (!notReadyIpSet.contains(address.getIp())) {
                    instances.add(new K8sServiceInstance(namespace, serviceName, address.getIp(), port.getPort()));
                }
            }
        }
        return instances;
    }

    /**
     * 销毁客户端
     */
    @Override
    @PreDestroy
    public void destroy() {
        if (null != client && destroy.compareAndSet(false, true)) {
            serviceWatchMap.clear();
            serviceMap.clear();
            client.close();
        }
    }
}
