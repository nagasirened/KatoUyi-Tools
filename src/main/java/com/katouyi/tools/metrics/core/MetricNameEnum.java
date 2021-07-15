package com.katouyi.tools.metrics.core;

/**
 * @Author: zhtao
 */
public enum MetricNameEnum {


    /**
     * DefaultV4 召回条数
     */
    RECALL_SIZE("recall_size"),
    /**
     * 接口 耗时
     */
    INTERFACE_TIMER("interface_timer"),
    /**
     * 接口 耗时
     */
    METHOD_TIMER("method_timer"),
    /**
     * 模型 接口 耗时
     */
    METHOD_MODEL_TIMER("method_model_timer"),
    /**
     * 线程池拒绝数
     */
    THREAD_POOL_REJECTED("thread_pool_rejected"),
    /**
     * 线程池监控
     */
    THREAD_POOL_MONITOR("thread_pool_monitor"),
    /**
     * 超时
     */
    TIME_OUT("time_out"),
    /**
     * Gauge
     */
    GAUGE("gauge"),
    /**
     * counter
     */
    COUNTER("counter"),
    /**
     * musicProportion
     */
    MUSIC_PROPORTION("music_proportion"),

    /**
     * musicProportionCounter
     */
    MUSIC_PROPORTION_COUNTER("music_proportion_counter"),

    ;

    private String name;


    MetricNameEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return "reco_api_" + name;
    }
}
