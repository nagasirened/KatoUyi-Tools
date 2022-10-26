package com.katouyi.tools.traffic;

import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class RedissonLimiter {

    public static RedissonClient getInstance() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        config.setTransportMode( TransportMode.NIO);
        config.setCodec( JsonJacksonCodec.INSTANCE);
        return Redisson.create(config);
    }

    public static void main(String[] args) throws InterruptedException {
        RedissonClient instance = getInstance();
        RRateLimiter rateLimiter = instance.getRateLimiter("test_limiter");
        rateLimiter.setRate( RateType.PER_CLIENT, 10, 1, RateIntervalUnit.SECONDS);
        long t1 = System.currentTimeMillis();
        for ( int i = 1; i <= 20; i++ ) {
            if ( rateLimiter.tryAcquire() ) {
                System.out.println("获取到锁" + i + "  " + (System.currentTimeMillis() - t1));
            } else {
                System.err.println("没有获取锁" + i + "  " + (System.currentTimeMillis() - t1));
            }
        }
        rateLimiter.acquire();
        for ( int i = 22; i <= 40; i++ ) {
            if ( rateLimiter.tryAcquire() ) {
                System.out.println("获取到锁" + i + "  " + (System.currentTimeMillis() - t1));
            } else {
                System.err.println("没有获取锁" + i + "  " + (System.currentTimeMillis() - t1));
            }
        }

        instance.shutdown();
    }

}
