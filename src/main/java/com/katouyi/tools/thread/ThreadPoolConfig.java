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

    @Primary
    @Bean(name = "recallExecutorModel")
    public ThreadPoolExecutorMdcWrapper configThreadPoolRecallModel() {
        return new ThreadPoolExecutorMdcWrapper(
                10,
                15,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(150),
                new DefaultThreadFactory("recall-model-thread-pool"),
                new KyRejectRecallModelHandler());
    }

    @Primary
    @Bean(name = "rankExecutorModel")
    public ThreadPoolExecutorMdcWrapper configThreadPoolRankModel() {
        return new ThreadPoolExecutorMdcWrapper(
                15,
                20,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(150),
                new DefaultThreadFactory("rank-model-thread-pool"),
                new KyRejectRankModelHandler());
    }

    @Primary
    @Bean(name = "asyncThreadPool")
    public ThreadPoolExecutorMdcWrapper asyncThreadPoolTaskExecutor() {
        return new ThreadPoolExecutorMdcWrapper(
                3,
                5,
                300,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(30000),
                new DefaultThreadFactory("async-thread-pool"),
                new KyRejectAsyncThreadPoolHandler());
    }


    class KyRecallSourceRejectHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            // MetricUtils.metricsThreadPoolRejected(MetricConstant.THREAD_POOL_RECALL_SOURCE);
            throw new RejectedExecutionException("多路召回RecallSource线程池 Task " + task.toString() + " rejected from " + executor.toString());
        }
    }

    class KyRejectRecallModelHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            // MetricUtils.metricsThreadPoolRejected(MetricConstant.THREAD_POOL_RECALL_MODEL);
            throw new RejectedExecutionException("召回模型RecallModel线程池 Task " + task.toString() + " rejected from " + executor.toString());
        }
    }

    class KyRejectRankModelHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            // MetricUtils.metricsThreadPoolRejected(MetricConstant.THREAD_POOL_RANK_MODEL);
            throw new RejectedExecutionException("排序模型RankModel线程池 Task " + task.toString() + " rejected from " + executor.toString());
        }
    }

    /**
     * 告警，但不抛错
     */
    class KyRejectAsyncThreadPoolHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            // MetricUtils.metricsThreadPoolRejected(MetricConstant.THREAD_POOL_ASYNC);
        }
    }

}