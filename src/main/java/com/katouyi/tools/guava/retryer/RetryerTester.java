package com.katouyi.tools.guava.retryer;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * <guava-retry.version>2.0.0</guava-retry.version>
 * <dependency>
 *       <groupId>com.github.rholder</groupId>
 *       <artifactId>guava-retrying</artifactId>
 *       <version>${guava-retry.version}</version>
 * </dependency>
 */
public class RetryerTester {

    public static void main(String[] args) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                // .retryIfException()
                // 如果是空指针或者状态异常则重试
                .retryIfException(Predicates.or(Predicates.instanceOf(NullPointerException.class),
                                                Predicates.instanceOf(IllegalStateException.class)))
                // 5秒后重试
                .withWaitStrategy( WaitStrategies.fixedWait( 5, TimeUnit.SECONDS ) )
                // 重试三次后停止
                .withStopStrategy( StopStrategies.stopAfterAttempt( 3 ) )
                // 重试执行最多3s
                .withAttemptTimeLimiter( AttemptTimeLimiters.fixedTimeLimit( 3, TimeUnit.SECONDS ) )
                .build();
    }

}
