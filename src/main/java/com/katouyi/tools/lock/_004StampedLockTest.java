package com.katouyi.tools.lock;

import cn.hutool.core.convert.Convert;

import java.util.Date;
import java.util.concurrent.locks.StampedLock;

public class _004StampedLockTest {

    private final StampedLock lock = new StampedLock();

    private double x;
    private double y;

    /**
     * 写入
     */
    public void write(double x, double y) {
        long timeStamped = lock.writeLock();  // 获取写锁
        try {
            this.x = x;
            this.y = y;
        } finally {
            lock.unlockWrite(timeStamped);    // 释放写锁
        }
    }

    /**
     * 读取
     */
    public double read() {
        long timeStamped = lock.tryOptimisticRead();
        double x1 = x;
        double y1 = y;
        if (!lock.validate(timeStamped)) {       // 验证，如果不正确，则获取读锁重新获取
            timeStamped = lock.readLock();
            try {
                x1 = x;                          // 重新获取
                y1 = y;
            } finally {
                lock.unlockRead(timeStamped);    // 释放锁
            }
        }
        return Math.sqrt(x1 * x1 + y1 * y1);
    }

    public static void main(String[] args) {
        String a = "2017/05/06";
        Date value = Convert.toDate(a);
        System.out.println(value);
    }
}
