package com.katouyi.tools.rocket.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "orderConsumer", topic = "orderTopic", consumeMode = ConsumeMode.ORDERLY)
public class RockerConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String value) {
        log.info("Received a message, value is {}", value);
    }
}
