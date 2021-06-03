package com.katouyi.tools.lock;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class _007FutureTaskTest {

    /**
     * Runnable没有返回值
     * Callable仅仅是一个接口，实现call。它不能直接给线程运行，需要使用一个包装类FutureTask
     *
     * FutureTask 继承了 Runnable,并封装Callable为属性
     * FutureTask的run()方法里面调用了Callable的call()方法
     *
     * 🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟
     * FutureTask 源码的逻辑：
     *      run方法调用后，调用Callable的call方法，完成调用后有个Set方法，这个方法会unpark等待的线程
     *      如果get()调用时，方法没有run完，那么会将线程给park一下，等到上面的set方法调用了，又会调用unpark了
     *      就算没有park，调用unpark也没有关系，后进来的get就不会阻塞，直接获取结果了
     *
     *      内置一下等待调用的线程队列，set完成后会遍历唤醒已经获取了结果的等待的线程
     * 🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟
     */
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        /**
         * 任务1
         */
        Callable<JSONObject> callable = new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                String response = Optional.ofNullable(restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=20210430", String.class)).orElse("");
                return (JSONObject)(new JSONObject().put("test1", response));
            }
        };
        FutureTask<JSONObject> futureTask1 = new FutureTask<>(callable);
        new Thread(futureTask1).start();

        /**
         * 任务2
         */
        FutureTask<JSONObject> futureTask2 = new FutureTask<>(() -> {
            String response = Optional.ofNullable(restTemplate.getForObject("http://tool.bitefu.net/jiari/?d=20210501", String.class)).orElse("");
            return (JSONObject) (new JSONObject().put("test2", response));
        });
        new Thread(futureTask2).start();


        try {
            // get方法会等待任务执行结束
            // JSONObject jsonObject = futureTask.get(1, TimeUnit.SECONDS);
            JSONObject jsonObject = futureTask1.get();
            JSONObject jsonObject1 = futureTask2.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
