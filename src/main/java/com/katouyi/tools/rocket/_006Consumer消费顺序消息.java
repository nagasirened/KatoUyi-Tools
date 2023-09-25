package com.katouyi.tools.rocket;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Arrays;
import java.util.List;

public class _006Consumer消费顺序消息 {

    public static void main(String[] args) throws Exception {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer_group");
        // 设置nameServer地址
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅一个或者多个Topic，以及tag来过滤需要消费的消息
        consumer.subscribe("TopicTest", "*");

        /**
         * MessageListenerOrderly           一个队列的消息就会使用一个线程去处理
         * MessageListenerConcurrently
         */
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                for (MessageExt msg : msgs) {
                    System.out.println("线程名称:" + Thread.currentThread().getName() + " msg is" + Arrays.toString(msg.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
    }
}

