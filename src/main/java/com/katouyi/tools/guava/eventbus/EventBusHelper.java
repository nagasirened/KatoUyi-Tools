package com.katouyi.tools.guava.eventbus;

import cn.hutool.core.convert.Convert;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Guava发布订阅模式
 *
 * <dependency>
 *     <groupId>com.google.guava</groupId>
 *     <artifactId>guava</artifactId>
 *     <version>{guava.version}</version>
 * </dependency>
 */
@UtilityClass
public final class EventBusHelper {

    /** 同步执行的发布器，里面只有单线程执行 */
    public static final int ALIVE_TIME = 60;
    public static final int QUEUE_SIZE = 100;

    public EventBus create() {
        return create(EventBusType.SYNC);
    }

    public EventBus create( EventBusType type ) {
        if ( EventBusType.ASYNC.equals( type ) ) {
            int max = Runtime.getRuntime().availableProcessors();
            max = max < 0 ? 1 : max;
            int min = Convert.toInt( Math.max( 1, Math.floor( max / 2 ) ) );
            final ThreadPoolExecutor executor = new ThreadPoolExecutor( min, max, ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingDeque<>( QUEUE_SIZE ) );
            return new AsyncEventBus( executor );
        }
        return new EventBus();
    }

    public EventBus create(Executor executor) {
        return new AsyncEventBus( executor );
    }

    public enum EventBusType {
        SYNC,
        ASYNC,;
    }

}
