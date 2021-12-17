package com.katouyi.tools.exercise.lock;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class _003ReadWriteLockTest {

    /**
     * 读加锁的作用是防止读的时候被写更改，导致读的不一致行
     * 假设obj的x，y是[0,1]，某个写线程修改成[2,3]，你读到的要么是[0,1]，要么是[2,3]，但是没有锁，你读到的可能是[0,3]
     *
     * ReadWriteLock 拆分读写两个锁，用在对应的位置方法上；  但是写之前要等读完，有点悲观锁的意思
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    Lock rLock = readWriteLock.readLock();
    Lock wLock = readWriteLock.writeLock();
    private int[] counts = new int[]{0, 0};

    public void inc() {
        wLock.lock();
        try {
            counts[0] += 1;
            counts[1] += 1;
        } finally {
            wLock.unlock();
        }
    }

    public int[] get() {
        rLock.lock();
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rLock.unlock();
        }
    }

}
