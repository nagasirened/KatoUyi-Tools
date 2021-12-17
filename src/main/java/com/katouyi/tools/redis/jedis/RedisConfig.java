package com.katouyi.tools.redis.jedis;

import com.ky.common.redis.core.RedisHandler;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Resource
    private RedisProperties redisProperties;

    @Primary
    @Bean("redisHandler-db0")
    public RedisHandler redisHandlerDb0() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(100);
        config.setMaxWaitMillis(1000 * 100);
        config.setTestOnBorrow(false);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(false);
        List<JedisShardInfo> infos = new ArrayList<>();
        for (String host : redisProperties.getHosts()) {
            String url = "redis://" + host + ":" + redisProperties.getPort() + "/" + 0;
            JedisShardInfo infoA = new JedisShardInfo(url);
            infos.add(infoA);
        }
        ShardedJedisPool shardPool = new ShardedJedisPool(config, infos, Hashing.CRC);
        return new RedisHandler(shardPool);
    }

}
