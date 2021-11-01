package com.katouyi.tools.traffic;

/**
 * 原理：匀速补水，每次漏水前补水，再看桶里有没有足够的水可以用
 *
 * 令牌桶
 */
public class TokenBucket {

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
     * @param rate 每秒放入令牌数量
     */
    public TokenBucket(int capacity, int rate) {
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
     * 获取令牌
     * @param quota 漏水量
     * @return 是否获取令牌成功
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
     * 获取令牌  阻塞等待
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
