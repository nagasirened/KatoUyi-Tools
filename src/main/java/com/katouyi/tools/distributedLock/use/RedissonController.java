package com.katouyi.tools.distributedLock.use;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


/**
 * author: ZGF
 *
 * 1.引入redisson依赖
 * 2.添加配置文件，完成Bean的配置 (用springboot-starter 依赖的话可以直接使用 redissonClient)
 */

@RestController
public class RedissonController {

    @Autowired
    private RedissonClient redisson;


    @GetMapping("/redisson")
    public String redisLock() throws InterruptedException {
        RLock rLock = redisson.getLock("order");
        try {
            rLock.lock(10, TimeUnit.SECONDS);
            System.out.println("获取了锁");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("释放了锁");
        }
        return "OK";
    }
}
