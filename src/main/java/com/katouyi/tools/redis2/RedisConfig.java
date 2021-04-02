package com.katouyi.tools.redis2;

import com.alibaba.fastjson.JSON;
import com.ky.common.redis.core.RedisHandler;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {
    @Resource
    private RedisProperties redisProperties;

    @Bean("redisHander-db0")
    @Primary
    public RedisHandler redisHanderDb0() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(100);
        config.setMaxWaitMillis(1000 * 100);
        config.setTestOnBorrow(false);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(false);
        List<JedisShardInfo> infos = new ArrayList<JedisShardInfo>();
        for (String host : redisProperties.getHosts()) {
            String url = "redis://" + host + ":" + redisProperties.getPort() + "/" + 0;
            JedisShardInfo infoA = new JedisShardInfo(url);
            infos.add(infoA);
        }
        ShardedJedisPool shardPool = new ShardedJedisPool(config, infos, Hashing.CRC);
        // log.info("redis cluster config,db0 init successful,redis detail:{}", JSON.toJSONString(infos));
        // return shardPool;
        return new RedisHandler(shardPool);
    }

}
