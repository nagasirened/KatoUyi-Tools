package com.katouyi.tools.metrics.core;

import cn.hutool.core.collection.CollUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsUtils {

    /**
     * 用来存放gauge
     */
    static final ConcurrentHashMap<String, AtomicLong> gaugeMap = new ConcurrentHashMap<>(128);

    /**
     * 召回数量计数
     */
    public static void metricsRecallCounter(List<?> list, String recallSource) {
        int listSize = 0;
        if (CollUtil.isNotEmpty(list)) {
            listSize = list.size();
        }
        String key = MetricNameEnum.RECALL_SIZE.getName() + recallSource;
        gaugeMap.put(key, new AtomicLong(listSize));
        Gauge.builder(MetricNameEnum.RECALL_SIZE.getName(), gaugeMap, gaugeMap -> gaugeMap.get(key).get())
                .tag(MetricConstant.RECALL_SOURCE, recallSource).register(Metrics.globalRegistry);
    }

    public static void metricsRecallCounter(int size, String recallSource) {
        String key = MetricNameEnum.RECALL_SIZE.getName() + recallSource;
        gaugeMap.put(key, new AtomicLong(size));
        Gauge.builder(MetricNameEnum.RECALL_SIZE.getName(), gaugeMap, gaugeMap -> gaugeMap.get(key).get())
                .tag(MetricConstant.RECALL_SOURCE, recallSource).register(Metrics.globalRegistry);
    }


    /**
     * 接口耗时
     */
    public static void interfaceTimer(Timer.Sample sample, String interfaceName) {
        Timer timer = Timer.builder(MetricNameEnum.INTERFACE_TIMER.getName()).tag(MetricConstant.INTERFACE_NAME, interfaceName)
                .publishPercentileHistogram().publishPercentiles(MetricConstant.PERCENTILES_ARRAY).register(Metrics.globalRegistry);
        sample.stop(timer);
    }

    /**
     * 方法耗时
     */
    public static void methodTimer(Timer.Sample sample, String methodName) {
        Timer timer = Timer.builder(MetricNameEnum.METHOD_TIMER.getName())
                .tag(MetricConstant.METHOD_NAME, methodName)
                .publishPercentileHistogram()
                .publishPercentiles(MetricConstant.PERCENTILES_ARRAY).register(Metrics.globalRegistry);
        sample.stop(timer);
    }

    /**
     * 记录 模型 方法耗时
     */
    public static void methodModelTimer(Timer.Sample sample, String methodName, String model) {
        Timer timer = Timer.builder(MetricNameEnum.METHOD_MODEL_TIMER.getName())
                .tags(MetricConstant.METHOD_NAME, methodName, "model", model)
                .publishPercentileHistogram()
                .publishPercentiles(MetricConstant.PERCENTILES_ARRAY).register(Metrics.globalRegistry);
        sample.stop(timer);
    }

    /**
     * 线程池 拒绝数量
     */
    public static void metricsThreadPoolRejected(String poolName) {
        Metrics.counter(MetricNameEnum.THREAD_POOL_REJECTED.getName(), Tags.of(MetricConstant.POOL_NAME, poolName)).increment();
    }

    /**
     * 线程池 监控
     */
    public static void metricsThreadPoolMonitor(int size, String parameterName) {
        String key = MetricNameEnum.THREAD_POOL_MONITOR.getName() + parameterName;
        gaugeMap.put(key, new AtomicLong(size));
        Gauge.builder(MetricNameEnum.THREAD_POOL_MONITOR.getName(), gaugeMap, gaugeMap -> gaugeMap.get(key).get())
                .tag(MetricConstant.PARAMETER_NAME, parameterName).register(Metrics.globalRegistry);
    }

    /**
     * 超时
     */
    public static void metricsTimeOut(String metricsName) {
        Metrics.counter(MetricNameEnum.TIME_OUT.getName(), Tags.of(MetricConstant.METRICS_NAME, metricsName)).increment();
    }

    /**
     * gauge
     */
    public static void metricsGauge(int size, String gaugeName) {
        String key = MetricNameEnum.GAUGE.getName() + gaugeName;
        gaugeMap.put(key, new AtomicLong(size));
        Gauge.builder(MetricNameEnum.GAUGE.getName(), gaugeMap, gaugeMap -> gaugeMap.get(key).get())
                .tag(MetricConstant.GAUGE_NAME, gaugeName).register(Metrics.globalRegistry);
    }

    /**
     * counter 统计
     */
    public static void metricsCounter(String counterName) {
        Metrics.counter(MetricNameEnum.COUNTER.getName(), Tags.of(MetricConstant.COUNTER_NAME, counterName)).increment();
    }

    /**
     * 统计music
     */
    public static void metricsMusicProportion(int size, String stageName, String tagName) {
        String key = MetricNameEnum.MUSIC_PROPORTION.getName() + stageName + tagName;
        gaugeMap.put(key, new AtomicLong(size));
        Gauge.builder(MetricNameEnum.MUSIC_PROPORTION.getName(), gaugeMap, gaugeMap -> gaugeMap.get(key).get())
                .tags(MetricConstant.STAGE_NAME, stageName, MetricConstant.TAG_NAME, tagName).register(Metrics.globalRegistry);
    }

    /**
     * 统计musicCounter
     */
    public static void metricsMusicProportionCounter(int size, String stageName, String tagName){
        Metrics.counter(MetricNameEnum.MUSIC_PROPORTION_COUNTER.getName(), Tags.of(MetricConstant.COUNTER_NAME, tagName,
                MetricConstant.STAGE_NAME,stageName)).increment(size);
    }
}
