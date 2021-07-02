package com.katouyi.tools.rocket;

import cn.hutool.core.date.DateUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class _005Producer发送顺序消息 {

    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("test_sync_producer_group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        String[] tags = new String[]{"tagA", "tagB", "tagC"};
        List<OrderStep> orderStepList = buildOrderSteps();
        String dateTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < 10; i++) {
            // 构建消息体
            String body = dateTime + " Hello RocketMQ " + orderStepList.get(i);
            Message message = new Message("TopicTest",
                    tags[i % tags.length],
                    "key" + i,
                    body.getBytes(StandardCharsets.UTF_8));

            /*SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                *//** 根据订单id选择发送queue *//*
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Long id = (Long) arg;
                    long index = id % mqs.size();
                    return mqs.get((int) index);
                }
            }, orderStepList.get(i).getOrderId());*/

            SendResult sendResult = producer.send(message, (mqs, msg, arg) -> {
                Long id = (Long) arg;
                long index = id % mqs.size();
                return mqs.get((int) index);
            }, orderStepList.get(i).getOrderId());

            System.out.println(String.format("SendResult status:%s, queueId:%d, body:%s",
                    sendResult.getSendStatus(),
                    sendResult.getMessageQueue().getQueueId(),
                    body));
        }
        producer.shutdown();
    }



    public static List<OrderStep> buildOrderSteps() {
        List<OrderStep> orderList = new ArrayList<OrderStep>();

        OrderStep orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("创建");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("付款");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111065L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("推送");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103117235L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        orderDemo = new OrderStep();
        orderDemo.setOrderId(15103111039L);
        orderDemo.setDesc("完成");
        orderList.add(orderDemo);

        return orderList;
    }

    /**
     * 订单的步骤
     */
    private static class OrderStep {
        private long orderId;
        private String desc;

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return "OrderStep{" +
                    "orderId=" + orderId +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}

