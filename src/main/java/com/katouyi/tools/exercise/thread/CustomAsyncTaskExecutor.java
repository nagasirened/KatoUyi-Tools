package com.katouyi.tools.exercise.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 🌟🌟🌟 默认的AsyncTaskExecutor，不能进行异常处理，因此增加这个
 */
public class CustomAsyncTaskExecutor implements AsyncTaskExecutor {

    private final Logger log = LoggerFactory.getLogger(CustomAsyncTaskExecutor.class);

    private AsyncTaskExecutor executor;

    public CustomAsyncTaskExecutor(AsyncTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable runnable, long l) {
        executor.execute(packageRunnable(runnable), l);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return submit(packageRunnable(runnable));
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(packageCallable(callable));
    }

    @Override
    public void execute(Runnable runnable) {
        executor.execute(packageRunnable(runnable));
    }

    private Runnable packageRunnable(Runnable runnable){
        return new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    handlerException(e);
                }
            }
        };
    }

    private <T> Callable packageCallable(Callable<T> callable){
        return new Callable() {
            @Override
            public T call() throws Exception {
                try {
                    return callable.call();
                } catch (Exception e) {
                    handlerException(e);
                    throw e;
                }
            }
        };
    }

    private void handlerException(Exception e) {
        log.error("CustomAsyncTaskExecutor#handlerException, error", e);
    }
}
