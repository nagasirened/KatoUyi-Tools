package com.katouyi.tools.exercise.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class _001ReentranLockTest {

    private final Lock lock = new ReentrantLock();
    private int count;

    /**
     * ReentrantLock 简单示例
     * @param n
     */
    public void add(int n) {
        lock.lock();
        try {
            count += n;
        } finally {
            lock.unlock();
        }
    }

    // lock可以尝试获取锁，一定时间没有获取锁可以
    public void add2(int n) throws InterruptedException {
        if (lock.tryLock(1, TimeUnit.SECONDS)) {
            try {
                // do some things
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            // do some other things
        }
    }
}
