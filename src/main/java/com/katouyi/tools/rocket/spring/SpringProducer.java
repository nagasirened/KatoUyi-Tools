package com.katouyi.tools.rocket.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringProducer {


    /***
     * 自动注解类
     * RocketMQAutoConfiguration
     *
     */



    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    @Qualifier("tsRocketMQTemplate")
    private RocketMQTemplate tsRocketMQTemplate;

    public void sendMsg(String topic, String msg) {
        log.info("发送报文：" + msg);
        rocketMQTemplate.convertAndSend(topic, msg);
        // 延迟且可以重复发送
        rocketMQTemplate.syncSend(topic, new GenericMessage<>(msg), 3000, 3);
    }

    /**
     * 发送事务消息
     */
    public void sendTsMsg(String topic, String msg) throws Exception {
        // 两段话是一个意思
        tsRocketMQTemplate.sendMessageInTransaction(topic, new GenericMessage<>(msg), null);
        // tsRocketMQTemplate.getProducer().sendMessageInTransaction(new Message(topic, msg.getBytes()), null);
    }
}