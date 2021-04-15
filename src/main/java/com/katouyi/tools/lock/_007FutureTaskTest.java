package com.katouyi.tools.lock;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class _007FutureTaskTest {

    /**
     * Runnable没有返回值
     * Callable仅仅是一个接口，实现call。它不能直接给线程运行，需要使用一个包装类FutureTask
     *
     * FutureTask 继承了 Runnable,并封装Callable为属性
     * FutureTask的run()方法里面调用了Callable的call()方法
     */
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        Callable<JSONObject> callable = new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                String response = Optional.ofNullable(restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=20210430", String.class)).orElse("");
                return (JSONObject)(new JSONObject().put("test1", response));
            }
        };
        FutureTask<JSONObject> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        try {
            JSONObject jsonObject = futureTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
