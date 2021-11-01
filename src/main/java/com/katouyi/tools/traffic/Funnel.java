package com.katouyi.tools.traffic;

/**
 * 原理：匀速补水，每次漏水前补水，再看桶里有没有足够的水可以用
 *
 * 流量控制漏桶算法
 */
public class Funnel {

    /**
     * 1s  1000ms
     */
    private static final long NANO = 1000000000;
    private final long capacity; // 水量上限，补出多的将被抛弃
    private long leftQuota;      // 桶里剩余的水量
    private long leakingTs;      // 当前的时间戳
    private final int rate;      // 单位秒补水的量

    /**
     * 构造函数
     *
     * @param capacity 容量
     * @param rate 每秒漏水数量
     */
    public Funnel(int capacity, int rate) {
        this.capacity = capacity;
        this.leakingTs = System.nanoTime();
        this.rate = rate;
    }

    /**
     * 补水
     */
    private void makeSpace() {
        long now = System.nanoTime();
        long time = now - leakingTs;
        long leaked = time * rate / NANO;
        if (leaked < 1) {
            return;
        }
        leftQuota += leaked;

        if (leftQuota > capacity) {
            leftQuota = capacity;
        }
        leakingTs = now;
    }

    /**
     * 漏水。桶里水量不够就返回false
     * @param quota 漏水量
     * @return 是否漏水成功
     */
    public boolean tryWatering(int quota) {
        makeSpace();
        long left = leftQuota - quota;
        if (left >= 0) {
            leftQuota = left;
            return true;
        }
        return false;
    }

    /**
     * 漏水。没水就阻塞直到蓄满足够的水
     * @param quota 要漏的数量
     */
    public void watering(int quota) {
        long left;
        try {
            do {
                makeSpace();
                left = leftQuota - quota;
                if (left >= 0) {
                    leftQuota = left;
                } else {
                    Thread.sleep(1);
                }
            } while (left < 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
