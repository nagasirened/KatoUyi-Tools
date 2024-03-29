package com.katouyi.tools.traffic;

public class LeakingBarrel {

    /**
     * 水流出的速率
     */
    private final long rate;

    /**
     * 桶的大小
     */
    private final int burst;

    /**
     * 最后更新时间
     */
    private long refreshTime;

    /**
     * 桶里剩余的水量
     */
    private int water;

    public LeakingBarrel(long rate, int burst) {
        this.rate = rate;
        this.burst = burst;
        this.refreshTime = System.currentTimeMillis();
    }

    public void refreshWater() {
        long now = System.currentTimeMillis();
        long cost = (now - refreshTime) * rate / 1000;
        if (cost < 1) {
            return;
        }
        water -= cost;
        if (water < 0) {
            water = 0;
        }
        refreshTime = now;
    }

    /**
     * 非阻塞加水
     */
    public boolean tryAddWater(int quota) {
        refreshWater();
        int will = water + quota;
        if (will <= burst) {
            water = will;
            return true;
        }
        return false;
    }

    /**
     * 阻塞加水
     */
    public void addWater(int quota) {
        int will;
        try {
            do {
                refreshWater();
                will = water + quota;
                if (will <= burst) {
                    water = will;
                } else {
                    Thread.sleep(1);
                }
            } while (will > burst);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
