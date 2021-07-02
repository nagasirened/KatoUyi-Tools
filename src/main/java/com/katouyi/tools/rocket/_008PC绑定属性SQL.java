package com.katouyi.tools.rocket;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class _008PC绑定属性SQL {

    public static void main(String[] args) throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("producerGroup");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message message = new Message("sqlFilter", "tagA",("delayValue" + i).getBytes(StandardCharsets.UTF_8));
            // 给message绑定一个属性，用于过滤
            message.putUserProperty("i", String.valueOf(i));

            producer.send(message);
        }
        producer.shutdown();
    }

    public static void main2(String[] args) throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerGroup");
        consumer.setNamesrvAddr("localhost:9876");
        /**
         * 只消费自定义属性中 i > 5的消息  ♾ 🌟 🈷️
         * TODO 支付的符号  > < >= <= = IN <>
         *     IS NULL    IS NOT NULL     AND     OR   NOT
         *
         * 例子： a between 0 and 5
         *       a > 5 and b = 'abc'
         */
        consumer.subscribe("sqlFilter", MessageSelector.bySql("i > 5"));


        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(System.out::println);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }
}
