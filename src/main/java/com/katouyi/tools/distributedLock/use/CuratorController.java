package com.katouyi.tools.distributedLock.use;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


/**
 * author: ZGF
 *
 * 1. 加入依赖
 * 2. 创建Bean，配置类是CuratorConfig
 * 3. 实现代码如下：获取锁 lock.acquire(timelong, TimeUnit)    释放锁lock.release()
 */

@RestController
public class CuratorController {

    @Autowired
    private CuratorFramework curatorFramework;

    @GetMapping("/curator")
    public String curatorLock() throws Exception {
        // 分布式锁
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/curator");
        if (lock.acquire(10, TimeUnit.SECONDS)) {
            try {
                System.out.println("获取了锁");
                Thread.sleep(5000);
            } finally {
                lock.release();
                System.out.println("释放锁");
            }
        }
        return "OK";
    }
}
