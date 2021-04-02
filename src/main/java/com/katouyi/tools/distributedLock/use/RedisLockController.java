package com.katouyi.tools.distributedLock.use;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * author: ZGF
 * context :
 */

@RestController
public class RedisLockController {

    final String LUA_DELETE_LOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/redisnx")
    public String redisLock() throws InterruptedException {
        // 获取锁
        System.out.println("进入方法");
        String randomValue = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent( "redisNx", randomValue, 10, TimeUnit.SECONDS);
        if (lock) {
            try{
                System.out.println("获取到锁了");
                Thread.sleep(5000);
            } finally {
                stringRedisTemplate.execute(RedisScript.of(LUA_DELETE_LOCK, Long.class),
                                            Collections.singletonList("redisNx"),
                                            randomValue);
                System.out.println("释放锁成功");
            }
        }
        return "OK";
    }
}
