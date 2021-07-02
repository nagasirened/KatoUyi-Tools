package com.katouyi.tools.rocket.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SpringProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMsg(String topic, String msg)
    {
        log.info("发送报文：" + msg);
        rocketMQTemplate.convertAndSend(topic, msg);
    }
}