package com.katouyi.tools.traffic;

import com.google.common.util.concurrent.RateLimiter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiterTest {

    public static void main(String[] args) throws InterruptedException {
        int nTasks = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(nTasks);
        CountDownLatch countDownLatch = new CountDownLatch(nTasks);
        long start = System.currentTimeMillis();

        AtomicInteger index = new AtomicInteger( 0 );
        List<Runnable> runnableList = new LinkedList<>();
        for ( int i = 0; i < 10; i++ ) {
            runnableList.add( new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep( 1000 );
                    } catch ( InterruptedException ignored ) {
                    } finally {
                        System.out.println( Thread.currentThread().getName() + " gets job " + index.getAndAdd( 1 ) + " done " + (System.currentTimeMillis() - start));
                        countDownLatch.countDown();
                    }
                }
            } );
        }



        // qps设置为5，代表一秒钟只允许处理五个并发请求
        // RateLimiter rateLimiter = RateLimiter.create(2, 5, TimeUnit.SECONDS);
        RateLimiter rateLimiter = RateLimiter.create(5);

        // 提前预支了令牌
        // rateLimiter.acquire();
        // Thread.sleep( 1000 );
        for ( Runnable runnable : runnableList ) {
            rateLimiter.acquire();
            executorService.execute( runnable );
        }
        countDownLatch.await();
        System.out.println("10 jobs gets done by 5 threads concurrently in " + (System.currentTimeMillis() - start) + " milliseconds");

        /*Thread.sleep( 5000 );
        for ( Runnable runnable : runnableList ) {
            rateLimiter.acquire();
            executorService.execute( runnable );
        }*/
        executorService.shutdown();
    }


}
