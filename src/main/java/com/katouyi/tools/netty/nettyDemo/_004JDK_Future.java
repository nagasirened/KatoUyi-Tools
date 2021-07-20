package com.katouyi.tools.netty.nettyDemo;

import java.util.concurrent.*;

public class _004JDK_Future {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("开始计算结果");
                TimeUnit.SECONDS.sleep(1);
                return 50;
            }
        });

        System.out.println("等待结果");
        System.out.println("结果是：" + future.get());
        threadPool.shutdown();
    }
}
