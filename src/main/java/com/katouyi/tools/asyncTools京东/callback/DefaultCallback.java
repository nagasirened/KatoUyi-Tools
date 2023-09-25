package com.katouyi.tools.asyncTools京东.callback;

import com.katouyi.tools.asyncTools京东.worker.WorkerResult;

public class DefaultCallback<T, V> implements ICallback<T, V>{
    @Override
    public void result(boolean success, T param, WorkerResult<V> workResult) {
        // pass
    }
}
