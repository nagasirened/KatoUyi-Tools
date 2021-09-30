package com.katouyi.tools.thread;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.*;


/**
 * @author zhtao
 */
@Configuration
public class ThreadPoolConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Primary
    @Bean(name = "recallExecutor")
    public ThreadPoolExecutorMdcWrapper configThreadPoolRecallSource() {
        return new ThreadPoolExecutorMdcWrapper(
                10,
                15,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(150),
                new DefaultThreadFactory("recall-source-thread-pool"),
                new KyRecallSourceRejectHandler());
    }

    class KyRecallSourceRejectHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            // MetricUtils.metricsThreadPoolRejected(MetricConstant.THREAD_POOL_RECALL_SOURCE);
            throw new RejectedExecutionException("多路召回RecallSource线程池 Task " + task.toString() + " rejected from " + executor.toString());
        }
    }

}