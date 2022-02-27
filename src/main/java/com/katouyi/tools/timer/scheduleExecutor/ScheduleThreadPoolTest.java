package com.katouyi.tools.timer.scheduleExecutor;

import java.util.Date;
import java.util.concurrent.*;

public class ScheduleThreadPoolTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 单线程的
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        //ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


        /**
         * 线程异常，任务会被丢弃
         * 定时线程池中，最大线程数没有意义，执行时间距离当前时间越近排序在最前面
         * **🌟🌟 用于添加ScheduleFutureTask, 继承于FutureTask(实现RunnableScheduleFuture接口) 🌟🌟
         * 消息从DelayQueue中获取Task，实现Delayed接口，getDelay()方法获取延迟时间
         *
         * scheduleAtFixedRate      按照固定频次执行，如果任务耗时过长，第一个结束马上执行第二个
         *                          如果此任务的任何执行时间超过其周期，则后续执行可能会延迟开始，但不会并发执行。
         * scheduleWithFixedDelay   按照固定延迟执行，如果任务耗时过长，第二个相比第一个结束晚delay执行
         * schedule 只执行一次，传delay
         */
        /*for (int i = 0; i < 2; i++) {
            scheduledExecutorService.schedule(() -> System.out.println("一次性任务" + new Date().toString()), 10, TimeUnit.SECONDS);
        }*/

        // initialDelay 是首次延迟的时间
        /*scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                System.out.println("固定频次执行：" + new Date().toString());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10000, 2, TimeUnit.SECONDS);

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                System.out.println("=======固定延迟执行：" + new Date().toString());
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);*/

        ScheduledFuture<String> future = scheduledExecutorService.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "test";
            }
        }, 1, TimeUnit.SECONDS);
        // 阻塞等待
        System.out.println(future.get());

        scheduledExecutorService.shutdown();
    }

}
