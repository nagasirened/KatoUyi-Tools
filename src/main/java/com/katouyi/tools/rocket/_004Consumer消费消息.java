package com.katouyi.tools.rocket;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class _004Consumer消费消息 {

    public static void main(String[] args) throws InterruptedException, MQClientException {
        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer_group");
        // 设置nameServer地址
        consumer.setNamesrvAddr("localhost:9876");

        // 订阅一个或者多个Topic，以及tag来过滤需要消费的消息
        consumer.subscribe("test_topic_a", "test_tag_a || test_tag_b");

        consumer.setMessageModel(MessageModel.CLUSTERING);      // BROADCASTING 广播模式    CLUSTERING 默认集群模式

        // 注册回调实现类来处理从broker拉回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            // messageExtList 消息内容的集合
            // ConsumeConcurrentlyContext 是消费者信息集合， delayLevelWhenNextConsume默认是0，broker控制重试策略  >0 由client控制   <0 则不重试
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
                System.out.println("消息在此：" + JSON.toJSONString(messageExtList, true));
                // u can do sth.
                messageExtList.forEach(item -> System.out.println(String.valueOf(item.getBody())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者实例
        consumer.start();
    }
}
