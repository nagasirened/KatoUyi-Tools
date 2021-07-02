package com.katouyi.tools.rocket;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

public class _007Producer发送延迟消息 {

    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("test_sync_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message message = new Message("delayTopic", ("delayValue" + i).getBytes(StandardCharsets.UTF_8));
            // delay等级为3， 会延迟10s发送
            // "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h"
            message.setDelayTimeLevel(3);
            producer.send(message);
        }
        producer.shutdown();
    }
}
