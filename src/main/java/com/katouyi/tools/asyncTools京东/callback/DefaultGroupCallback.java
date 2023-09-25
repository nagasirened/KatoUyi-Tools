package com.katouyi.tools.asyncTools京东.callback;

import com.katouyi.tools.asyncTools京东.worker.WorkerWrapper;

import java.util.List;

public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper<?, ?>> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper<?, ?>> workerWrappers, Exception e) {

    }
}
