package com.katouyi.tools.metrics.custom;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static io.micrometer.core.instrument.Timer.*;

@Slf4j
@Component
public class CustomMetricsUtils {

    public static final String GAUGE_NAME = "-custom-gauge";
    public static final String TIMER_NAME = "-timer-gauge";
    public static final String COUNTER_NAME = "-counter-gauge";

    public static final String UNIQUE_TAG_KEY = "unique_tag";

    public static final double[] PERCENTILES_ARRAY = {0.5, 0.90, 0.99};

    @Value("${spring.application.name}")
    private String appName;

    /**
     * ==================================================================
     *                          gauge
     * ==================================================================
     */

    public void gaugeMetrics(Integer number, Map<String, String> tagMap) {
        if (tagMap.isEmpty()) {
            log.error("CustomMetricsUtils#gaugeMetrics, there's no tag for metrics");
            return;
        }
        Gauge.builder(pieceName(GAUGE_NAME), number, item -> Double.parseDouble(String.valueOf(item)))
                .tags(parseTag(tagMap))
                .register(Metrics.globalRegistry);
    }

    public void gaugeMetrics(Integer number, String uniqueTag) {
        String gaugeName = appName + GAUGE_NAME;
        Gauge.builder(gaugeName, number, item -> Double.parseDouble(String.valueOf(item)))
                .tags(UNIQUE_TAG_KEY, uniqueTag)
                .register(Metrics.globalRegistry);
    }

    /**
     * ==================================================================
     *                          timer
     * ==================================================================
     */

    public void timerMetrics(Sample sample, String className, String methodName) {
        // Timer.Sample sample = Timer.start();  开启打点
        // Thread.currentThread().getStackTrace()[0].getClassName();  获取当前栈针的类名和方法名
        Timer timer = builder(pieceName(TIMER_NAME)).tags("class", className, "method", methodName)
                .publishPercentiles(PERCENTILES_ARRAY).register(Metrics.globalRegistry);
        sample.stop(timer);
    }

    /**
     * ==================================================================
     *                          counter
     * ==================================================================
     */
    public void counterIncr(Map<String, String> tagMap) {
        counterIncr(tagMap, 1);
    }

    public void counterIncr(String uniqueTag) {
        counterIncr(uniqueTag, 1);
    }

    public void counterIncr(String uniqueTag, int number) {
        Counter counter = Counter.builder(pieceName(COUNTER_NAME))
                .tag(UNIQUE_TAG_KEY, uniqueTag)
                .register(Metrics.globalRegistry);
        counter.increment(number);
    }

    public void counterIncr(Map<String, String> tagMap, int number) {
        Counter counter = Counter.builder(pieceName(COUNTER_NAME))
                .tags(parseTag(tagMap))
                .register(Metrics.globalRegistry);
        counter.increment(number);
    }

    /**
     * 拼接查询索引词
     */
    private String pieceName(String type) {
        return appName + type;
    }

    /**
     * 解析tagMap
     */
    private String[] parseTag(Map<String, String> tagMap) {
        Iterator<Map.Entry<String, String>> iter = tagMap.entrySet().iterator();
        String[] tagGroup = new String[tagMap.size() << 1];
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            tagGroup[index++] = entry.getKey();
            tagGroup[index++] = entry.getValue();
        }
        return tagGroup;
    }

    /**
     * 解析tagMap2
     */
    private Iterable<String> parseTag2(Map<String, String> tagMap) {
        Iterator<Map.Entry<String, String>> iter = tagMap.entrySet().iterator();
        ArrayList<String> tagGroup = new ArrayList<>(tagMap.size() << 1);
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            tagGroup.add(entry.getKey());
            tagGroup.add(entry.getValue());
        }
        return tagGroup;
    }
}
