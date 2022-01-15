package com.katouyi.tools.distributedLock.config;

import com.katouyi.tools.algorithm.User;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.api.listener.BasePatternStatusListener;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * author: ZGF
 * context : 配置redisson客户端
 */

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient () {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.setTransportMode(TransportMode.NIO);
        config.setCodec(JsonJacksonCodec.INSTANCE);
        return Redisson.create(config);
    }

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://172.16.238.150:27005/3"); // .setPassword("qwer1011");
        RedissonClient client = Redisson.create(config);

        // Keys
        RKeys keys = client.getKeys();
        Iterable<String> keysByPattern1 = keys.getKeysByPattern("test*");    //默认是scan 10个
        Iterable<String> keysByPattern2 = keys.getKeysByPattern("test*", 10);
        keys.renamenx("test1", "test2");
        keys.delete("");

        // bucket
        RBucket<Object> bucket = client.getBucket("bucket");
        bucket.set(23);
        bucket.set("sda");
        Object res = bucket.get();
        bucket.delete();

        // BitSet
        RBitSet bitset = client.getBitSet("bitset");
        bitset.set(12);     // 默认设置为true
        bitset.set(13, false);
        long bitsetLen = bitset.length();   // 返回“逻辑大小”=最高设置位加1的索引。如果没有任何设置位，则返回零。
        long size = bitset.size();          // 返回设置的位数
        bitset.set(12, 333);            // 将从索引(包含)到索引(不包含)的所有位设置为1  将12-332全部设置为1
        bitset.clear();
        bitset.xor("bitset2");  // 对该对象和指定的位集执行异或运算。

        // topic
        RTopic topic = client.getTopic("topic");
        topic.publish(new User());      // 消息的类型自己设定
        topic.publishAsync(new User());
        // 订阅者
        topic.addListener(User.class, new MessageListener<User>() {
            @Override
            public void onMessage(CharSequence charSequence, User s) {
                // 在Redis节点故障转移（主从切换）或断线重连以后，所有的话题监听器将自动完成话题的重新订阅
            }
        });

        // 模糊主题
        RPatternTopic patternTopic = client.getPatternTopic("patternTopic.*");
        patternTopic.addListener(User.class, new PatternMessageListener<User>() {
            @Override
            public void onMessage(CharSequence pattern, CharSequence channel, User user) {
                // 看参数名
            }
        });
        patternTopic.addListener(new BasePatternStatusListener() {
            @Override
            public void onPSubscribe(String channel) {
                super.onPSubscribe(channel);
            }
            @Override
            public void onPUnsubscribe(String channel) {
                super.onPUnsubscribe(channel);
            }
        });

        // bloom  RBloomFilter可自定义泛型
        RBloomFilter<String> bloomFilter = client.getBloomFilter("bloomFilter");
        bloomFilter.tryInit(500000, 0.0001);            // 初始化布隆过滤器，预计统计元素数量为 500000，期望误差率为 0.0001
        bloomFilter.add("14975");
        bloomFilter.contains("12344");

        // RateLimiter
        RRateLimiter rateLimiter = client.getRateLimiter("rateLimiter");
        rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
        boolean b = rateLimiter.tryAcquire();

        // Lock
        RLock lock = client.getLock("lock");
        lock.tryLock(1, 2, TimeUnit.SECONDS);


        client.shutdown();
    }
}

