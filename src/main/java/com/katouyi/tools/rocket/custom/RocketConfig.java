package com.katouyi.tools.rocket.custom;


import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketConfig {

    @Autowired
    private RocketMQProperties rocketMQProperties;

    @Bean
    @Qualifier("rocketMQTemplate")
    public RocketMQTemplate rocketMQTemplate() {
        return new RocketMQTemplate();
    }

    @Bean
    @Qualifier("tsRocketMQTemplate")
    public RocketMQTemplate tsRocketMQTemplate() {
        RocketMQTemplate tsRocketMQTemplate = new RocketMQTemplate();
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer(rocketMQProperties.getProducer().getGroup());
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                if ("tag1".equals(msg.getTags()))
                return LocalTransactionState.COMMIT_MESSAGE;
                return LocalTransactionState.UNKNOW;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                if ("tag2".equals(msg.getTags()))
                return LocalTransactionState.COMMIT_MESSAGE;
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        });

        tsRocketMQTemplate.setProducer(transactionMQProducer);
        return tsRocketMQTemplate;
    }

}
