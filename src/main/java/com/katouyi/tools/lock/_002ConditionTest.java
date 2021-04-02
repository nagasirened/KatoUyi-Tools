package com.katouyi.tools.lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 步骤2
 */
public class _002ConditionTest {

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private Queue<String> queue = new LinkedList<>();

    public void addTask(String s) {
        lock.lock();
        try {
            queue.add(s);
            condition.signalAll();      // 相当于notifyAll
        } finally {
            lock.unlock();
        }
    }

    public String getTask() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await();
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 　　add        增加一个元索                如果队列已满，则抛出一个IILegaISlabException异常
     * 　　remove     移除并返回队列头部的元素      如果队列为空，则抛出一个NoSuchElementException异常
     * 　　element    返回队列头部的元素           如果队列为空，则抛出一个NoSuchElementException异常
     * 　　offer      添加一个元素并返回true       如果队列已满，则返回false
     * 　　poll       移除并返问队列头部的元素      如果队列为空，则返回null
     * 　　peek       返回队列头部的元素           如果队列为空，则返回null
     * 　　put        添加一个元素                如果队列满，则阻塞
     * 　　take       移除并返回队列头部的元素      如果队列为空，则阻塞
     *
     *    remove、element、offer 、poll、peek 其实是属于Queue接口。
     */
}
