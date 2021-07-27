package com.katouyi.tools.distributedLock.config;

import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379").setPassword("qwer1011");
        RedissonClient client = Redisson.create(config);

        RMap<Object, Object> rMap = client.getMap("test1");
        rMap.put("k2", "v2");
        Object k1 = rMap.get("k1");
        System.out.println(k1);
        client.shutdown();
    }
}

