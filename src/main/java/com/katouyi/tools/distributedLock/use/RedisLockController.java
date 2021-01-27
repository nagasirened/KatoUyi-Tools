package com.katouyi.tools.distributedLock.use;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * author: ZGF
 * context :
 */

@RestController
public class RedisLockController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/redisnx")
    public String redisLock() throws InterruptedException {
        // 获取锁
        System.out.println("进入方法");
        String randomValue = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent( "redisnx", randomValue, 10, TimeUnit.SECONDS);
        if (lock) {
            try{
                System.out.println("获取到锁了");
                Thread.sleep(5000);
            } finally {
                stringRedisTemplate.opsForValue().setIfAbsent(/** 前缀 */"redisnx", randomValue, 10, TimeUnit.SECONDS);
                System.out.println("释放锁成功");
            }
        }
        return "OK";
    }
}
