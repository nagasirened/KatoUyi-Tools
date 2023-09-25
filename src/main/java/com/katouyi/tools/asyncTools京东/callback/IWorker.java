package com.katouyi.tools.asyncTools京东.callback;

import com.katouyi.tools.asyncTools京东.worker.WorkerWrapper;

import java.util.Map;

/**
 * 最小执行单元必须实现该接口
 * P param
 * R result
 */
public interface IWorker<T, V> {

    /**
     * 耗时操作
     * param        传递进来的参数，包装类
     * wrappers     所有的wrappers, 刚开始是一个空的Map, 每经历一个WorkerWrapper将自己包装进去
     */
    V action(T param, Map<String, WorkerWrapper<?, ?>> wrappers);

    /**
     *
     */
    V defaultValue();

}
