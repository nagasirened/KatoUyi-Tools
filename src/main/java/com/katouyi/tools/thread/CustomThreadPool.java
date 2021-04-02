package com.katouyi.tools.thread;

import com.ky.common.exception.AlarmLogger;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class CustomThreadPool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Primary
    @Bean(name = "asyncThreadPool")
    public ThreadPoolExecutor asyncThreadPoolTaskExecutor() {
        return new ThreadPoolExecutor(
                3,
                5,
                300,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(30000),
                new DefaultThreadFactory("async-thread-pool-thread"),
                new KyRejectAsyncThreadPoolHandler());
    }

    /**
     * 告警，但不抛错
     */
    class KyRejectAsyncThreadPoolHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            AlarmLogger.error("async-thread-pool-thread 异步处理线程池 task rejected. " + executor.toString());
            logger.error("async-thread-pool-thread 异步处理线程池 task rejected. " + executor.toString());
        }
    }

    /**
     * 可伸缩的线程池，核心为0,任务越多,线程越多,线程最多设置为10000
     * （线程处理一个任务完了，马上处理下一个任务，这样还不能处理完成的话就创建线程）
     */
    @Bean(name = "scalableThreadPool")
    public ThreadPoolExecutor scalableThreadPool() {
        return new ThreadPoolExecutor(0,
                10000,
                60,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),  // 默认非公平，填参数true是公平队列
                new DefaultThreadFactory("async-thread-pool-thread"),
                new KyRejectAsyncThreadPoolHandler()
        );
    }
}
