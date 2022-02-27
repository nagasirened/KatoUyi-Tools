package com.katouyi.tools.rocket;


import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class _009PC事务消息 {

    public static void main(String[] args) throws Exception{
        // 事务MQ
        TransactionMQProducer tsProducer = new TransactionMQProducer("tsProducer");
        tsProducer.setNamesrvAddr("localhost:9876");
        // 消息事务监听器
        tsProducer.setTransactionListener(new TransactionListener() {
            // 提交/回滚/UNKNOW
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                try {
                    // doSth
                    if (StringUtils.equals("1", "2")) {
                        return LocalTransactionState.COMMIT_MESSAGE;
                    }
                } catch (Exception e) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }

            // 查询回查消息事务状态
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // 看看回查的消息是不是tagC   正式环境，可以回查一些别的接口，用来判断是Commit或者rollBack
                System.out.println(msg.getTags());
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });

        tsProducer.start();
        List<String> tagList = Arrays.asList("tagA", "tagB", "tagC");
        for (int i = 0; i < 3; i++) {
            Message message = new Message("tsTopic", tagList.get(i), ("tsInfo" + i).getBytes(StandardCharsets.UTF_8));
            TransactionSendResult transactionSendResult = tsProducer.sendMessageInTransaction(message, null);
        }

        // 关闭了就没法回查了
        tsProducer.shutdown();
    }

}
