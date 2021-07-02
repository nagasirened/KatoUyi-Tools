package com.katouyi.tools.rocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Slf4j
public class _002Producer发送异步消息 {

    public static void main(String[] args) throws Exception {
        // 实例化生产者   参数是生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("test_async_producer_group");
        // 设置nameServer地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动producer实例
        producer.start();
        // 异步发送失败重试次数
        producer.setRetryTimesWhenSendAsyncFailed(0);

        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            int index = i;
            Message message = new Message("test_topic_b",
                    "test_tag_b",
                    ("message" + index).getBytes(StandardCharsets.UTF_8));

            // 发送异步消息
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("consumer success: {}" + sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error("consumer fail: {}", throwable.getMessage(), throwable);
                }
            });
        }

        // 等待5s
        countDownLatch.await(5, TimeUnit.SECONDS);
        producer.shutdown();
    }
}
