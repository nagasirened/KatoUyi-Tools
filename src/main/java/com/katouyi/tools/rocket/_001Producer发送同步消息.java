package com.katouyi.tools.rocket;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

public class _001Producer发送同步消息 {

    public static void main(String[] args) throws Exception {
        // 实例化生产者   参数是生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("test_sync_producer_group");
        // 设置nameServer地址
        producer.setNamesrvAddr("localhost:9876");
        // 启动producer实例
        producer.start();
        // producer.setRetryTimesWhenSendFailed(0);
        for (int i = 0; i < 100; i++) {
            // 主题 - tag - 消息体
            Message message = new Message("test_topic_a",
                    "test_tag_a",
                    ("message" + i).getBytes(StandardCharsets.UTF_8));

            // retryTimesWhenSendFailed:同步发送失败重投次数，默认为2，因此生产者会最多尝试发送retryTimesWhenSendFailed + 1次。
            // 不会选择上次失败的broker，尝试向其他broker发送，最大程度保证消息不丢。超过重投次数，抛出异常，由客户端保证消息不丢。
            // 当出现RemotingException、MQClientException和部分MQBrokerException时会重投。
            SendResult sendResult = producer.send(message);
            // 通过sendResult返回消息是否成功送达
            System.out.printf("%s%n", sendResult);
        }

        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }
}
