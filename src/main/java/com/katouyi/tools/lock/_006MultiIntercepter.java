package com.katouyi.tools.lock;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class _006MultiIntercepter {

    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();

        /**
         * 结合countDownLatch，同时调用两个接口并返回，能提升并发能力，减少接口消耗时间
         */
        CountDownLatch countDownLatch = new CountDownLatch(2);
        List<JSONObject> result = new ArrayList<>();
        new Thread(() -> {
            String response = Optional.ofNullable(restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=20210430", String.class)).orElse("");
            JSONObject resJSON = (JSONObject)(new JSONObject().put("test1", response));
            result.add(resJSON);
            countDownLatch.countDown();
        }).start();

        new Thread(() -> {
            String response = Optional.ofNullable(restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=20210501", String.class)).orElse("");
            JSONObject resJSON = (JSONObject)(new JSONObject().put("test2", response));
            result.add(resJSON);
            countDownLatch.countDown();
        }).start();
        try {
            countDownLatch.await(1, TimeUnit.SECONDS);
            for (JSONObject item : result) {
                System.out.println(item.toJSONString());
            }
        } catch (InterruptedException e) {
            return;
        }
    }
}
