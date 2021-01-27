package com.katouyi.tools.distributedLock.use;

import com.katouyi.tools.distributedLock.config.ZkLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * author: ZGF
 * context : ZkLock  ../config/ZkLock 里面的内容
 */

@RestController
public class ZkController {

    @GetMapping("/zk")
    public String zkLock() throws Exception {
        try (ZkLock zkLock = new ZkLock()){
            if (zkLock.getLock("test")) {
                Thread.sleep(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }
}
