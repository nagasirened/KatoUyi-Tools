package com.katouyi.tools.guava.retryer;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;

import java.util.concurrent.ExecutionException;

public class DefaultRetryerLister implements RetryListener {

    /**
     * Attempt接口有两个实现类，分别是
     * ResultAttempt 和 ExceptionAttempt
     */
    @Override
    public <V> void onRetry(Attempt<V> attempt) {
        // 第一次调用就认为是第一次了
        System.out.println("重试次数：" + attempt.getAttemptNumber());
        // 距离第一次重试的延时
        System.out.println("距离第一次过了：" + attempt.getDelaySinceFirstAttempt());
        // 重试结果: 是异常终止, 还是正常返回
        System.out.print(",hasException=" + attempt.hasException());
        System.out.print(",hasResult=" + attempt.hasResult());

        // 是什么原因导致异常
        if (attempt.hasException()) {
            System.out.print(",causeBy=" + attempt.getExceptionCause().toString());
        } else {
            // 正常返回时的结果
            System.out.print(",result=" + attempt.getResult());
        }

        // bad practice: 增加了额外的异常处理代码
        try {
            V result = attempt.get();
            System.out.print(",rude get=" + result);
        } catch ( ExecutionException e) {
            System.err.println("this attempt produce exception." + e.getCause().toString());
        }
    }

}
