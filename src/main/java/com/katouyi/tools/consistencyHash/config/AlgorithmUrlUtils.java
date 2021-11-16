package com.katouyi.tools.consistencyHash.config;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.katouyi.tools.consistencyHash.entity.K8sServiceInfo;
import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import com.katouyi.tools.consistencyHash.loadbalance.LoadBalance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class AlgorithmUrlUtils {
    private final Logger logger = LoggerFactory.getLogger(AlgorithmUrlUtils.class);
    public static final String SERVICE_NAME = "test-service-name";
    public static final String NAMESPACE = "test-namespace";

    @Resource
    private K8sClient k8sClient;

    @Resource(name = "consistentHashingLoadBalance")
    private LoadBalance loadBalance;

    /**
     * 一致性hash获取url
     * @param keyword       获取连接的关键词
     * @param defaultUrl    没有获取到url使用的默认地址
     * @return              调用服务的url
     */
    public String getAlgorithmUrl(String keyword, String defaultUrl, String deviceId) {
        String urlSuffix = "/xx/yy/zz";
        if (!StringUtils.isAnyBlank(urlSuffix, deviceId)) {
            try {
                LoadBalanceRequest request = new LoadBalanceRequest(deviceId, SERVICE_NAME);
                List<ServiceInstance> services = k8sClient.getServices(new K8sServiceInfo(NAMESPACE, SERVICE_NAME));
                if (CollectionUtil.isNotEmpty(services)) {
                    ServiceInstance instance = loadBalance.select(request, services);
                    if (Objects.nonNull(instance)) {
                        String lastUrl = "http://" + instance.getHost() + ":" + instance.getPort() + urlSuffix;
                        logger.info("AlgorithmUrlUtils#getAlgorithmUrl, last mix url, instance: {}, lastUrl: {}", JSON.toJSONString(instance), lastUrl);
                        return lastUrl;
                    }
                } else {
                    logger.info("AlgorithmUrlUtils#getAlgorithmUrl, there is no service list, keyword: {}, request: {}", keyword, JSON.toJSONString(request));
                }
            } catch (Exception e) {
                logger.error("AlgorithmUrlUtils#getAlgorithmUrl, get algorithm project url error, keyword: {}, urlSuffix: {}", keyword, urlSuffix);
            }
        }
        return defaultUrl;
    }

}
