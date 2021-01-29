package com.katouyi.tools.distributedLock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
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
    public RedissonClient redissonClient (@Value("${spring.redisson.host-port}") String redissonConnect) {
        Config config = new Config();
        config.useSingleServer().setAddress(redissonConnect);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}

