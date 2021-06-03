package com.katouyi.tools.distributedLock.use;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

public class RedissionTest {

    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379/").setPassword("qwer1011");
        RedissonClient redissonClient = Redisson.create(config);

        // RKeys keys = redissonClient.getKeys();
        // keys.getKeys().forEach(item -> System.out.println(item));

        // RBucket<Object> myBucket = redissonClient.getBucket("myBucket");
        // myBucket.set(1);
        // myBucket.set("string2");

        // RBitSet bitsetTest = redissonClient.getBitSet("bitsetTest");
        // bitsetTest.set(1, true);
        // bitsetTest.set(4, false);
        // System.out.println(bitsetTest.get(1));
        // System.out.println(bitsetTest.get(2));
        // System.out.println(bitsetTest.get(4));

        // RMap<Object, Object> hmp = redissonClient.getMap("hmp");
        // hmp.put("k1", "v1");
        // hmp.put("k2", true);
        // hmp.put("k3", 3D);
        // System.out.println(hmp.get("k2"));

        redissonClient.shutdown();
    }
}
