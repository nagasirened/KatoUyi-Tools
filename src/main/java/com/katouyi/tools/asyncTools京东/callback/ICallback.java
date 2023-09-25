package com.katouyi.tools.asyncTools京东.callback;

import com.katouyi.tools.asyncTools京东.worker.WorkerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每个执行单元执行完毕后，会回调该接口</p>
 * 需要监听执行结果的，实现该接口即可
 */
public interface ICallback<T, V> {
    Logger logger = LoggerFactory.getLogger(ICallback.class);

    default void begin() {
        // logger.info("callback thread: {}", Thread.currentThread().getId());
    }

    void result(boolean success, T param, WorkerResult<V> workResult);

}
