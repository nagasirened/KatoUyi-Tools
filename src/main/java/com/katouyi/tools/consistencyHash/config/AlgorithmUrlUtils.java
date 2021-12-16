package com.katouyi.tools.consistencyHash.config;

import com.katouyi.tools.consistencyHash.entity.LoadBalanceRequest;
import com.katouyi.tools.consistencyHash.entity.ServiceInstance;
import com.katouyi.tools.consistencyHash.loadbalance.LoadBalance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class AlgorithmUrlUtils {
    private final Logger logger = LoggerFactory.getLogger(AlgorithmUrlUtils.class);

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
                LoadBalanceRequest request = new LoadBalanceRequest(deviceId, K8sClient.SERVICE_NAME);
                ServiceInstance instance = loadBalance.select(request);
                if (Objects.nonNull(instance)) {
                    String lastUrl = "http://" + instance.getHost() + ":" + instance.getPort() + urlSuffix;
                    logger.info("AlgorithmUrlUtils#getAlgorithmUrl, last mix url, deviceId: {}, lastUrl: {}", deviceId, lastUrl);
                    return lastUrl;
                }
            } catch (Exception e) {
                logger.error("AlgorithmUrlUtils#getAlgorithmUrl, get algorithm project url error, keyword: {}, urlSuffix: {}", keyword, urlSuffix);
            }
        }
        return defaultUrl;
    }

}
