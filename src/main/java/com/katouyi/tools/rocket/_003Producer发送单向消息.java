package com.katouyi.tools.rocket;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class _003Producer发送单向消息 {

    public static void main(String[] args) throws Exception {
        // 实例化生产者   参数是生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("test_oneway_producer_group");
        // 设置nameServer地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动producer实例
        producer.start();

        int messageCount = 100;
        for (int i = 0; i < messageCount; i++) {
            int index = i;
            Message message = new Message("test_topic_c",
                    "test_tag_c",
                    ("message" + index).getBytes(StandardCharsets.UTF_8));

            // 发送单向消息，没有任何返回结果
            producer.sendOneway(message);
        }

        // 等待5s
        producer.shutdown();
    }
}
