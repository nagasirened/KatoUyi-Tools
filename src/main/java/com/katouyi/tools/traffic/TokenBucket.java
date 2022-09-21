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
    private final long capacity; // 令牌量上限，补出多的将被抛弃
    private long leftQuota;      // 桶里剩余的令牌量
    private long lastTime;      // 当前的时间戳
    private final int rate;      // 单位秒补水的量
    private final long millis;   // 单位时间(毫秒数)

    /**
     * 构造函数
     *
     * @param capacity 容量
     * @param rate 每秒放入令牌数量
     */
    public TokenBucket(int capacity, int rate, long millis) {
        this.capacity = capacity;
        this.lastTime = System.currentTimeMillis();
        this.rate = rate;
        this.millis = millis;
    }

    /**
     * 补水
     */
    private void makeSpace() {
        long now = System.currentTimeMillis();
        long time = now - lastTime;
        long leaked = time * rate / millis;
        if (leaked < 1) {
            return;
        }
        leftQuota += leaked;
        if (leftQuota > capacity) {
            leftQuota = capacity;
        }
        lastTime = now;
    }

    /**
     * 获取令牌
     * @param quota 需要获取的令牌数
     * @return      是否获取令牌成功
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
