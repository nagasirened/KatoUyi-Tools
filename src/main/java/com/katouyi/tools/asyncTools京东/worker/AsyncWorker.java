package com.katouyi.tools.asyncTools京东.worker;

import com.katouyi.tools.asyncTools京东.callback.DefaultGroupCallback;
import com.katouyi.tools.asyncTools京东.callback.IGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AsyncWorker {
    private static final Logger logger = LoggerFactory.getLogger(AsyncWorker.class);

    public static final ThreadPoolExecutor COMMON_POOL = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
            256, 15L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), (r) -> new Thread(r, "jd-async-worker"));

    public static boolean beginWork(long timeout, ThreadPoolExecutor poolExecutor, List<WorkerWrapper<?, ?>> workerWrappers) throws ExecutionException, InterruptedException{
        if (workerWrappers == null || workerWrappers.isEmpty()) {
            logger.error("there are no workers");
            return false;
        }
        int size = workerWrappers.size();
        ConcurrentHashMap<String, WorkerWrapper<?, ?>> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture<?>[] futures = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            WorkerWrapper<?, ?> workerWrapper = workerWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> workerWrapper.work(poolExecutor, timeout, forParamUseWrappers), poolExecutor);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<WorkerWrapper<?, ?>> set = new HashSet<>();
            totalWorkers(workerWrappers, set);
            for (WorkerWrapper<?, ?> wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }


    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     */
    public static boolean beginWork(long timeout, ThreadPoolExecutor pool, WorkerWrapper<?, ?>... workerWrapper) throws ExecutionException, InterruptedException {
        if(workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper<?, ?>> workerWrappers =  Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginWork(timeout, pool, workerWrappers);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static boolean beginWork(long timeout, WorkerWrapper<?, ?>... workerWrapper) throws ExecutionException, InterruptedException {
        return beginWork(timeout, COMMON_POOL, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, WorkerWrapper<?, ?>... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        CompletableFuture.runAsync(() -> {
            try {
                boolean success = beginWork(timeout, COMMON_POOL, workerWrapper);
                if (success) {
                    finalGroupCallback.success(Arrays.asList(workerWrapper));
                } else {
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                }
            } catch (ExecutionException | InterruptedException e) {
                finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
            }
        });
    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    private static void totalWorkers(List<WorkerWrapper<?, ?>> workerWrappers, Set<WorkerWrapper<?, ?>> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper<?, ?> wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<WorkerWrapper<?, ?>> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }

    }


    public static void shutDown() {
        COMMON_POOL.shutdown();
    }

    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() +
                "  completedCount " + COMMON_POOL.getCompletedTaskCount() +
                "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }
}
