package com.katouyi.tools.traffic;

/**
 * 滑动窗口限流
 */
public class SlideWindow {

    public long lastTime;               // 记录上一次请求的时间
    public final int windowInterval;    // 记录时间窗时间大小, 单位ms
    public final int number;            // 区间数量
    public final int limit;             // 流量限制
    public final int[] array;           // 计数数组
    public int index = 0;               // 当前处理窗口的下标

    public SlideWindow(int limit, int windowInterval, int number) {
        this.limit = limit;
        this.windowInterval = windowInterval;
        this.number = number;
        this.lastTime = System.currentTimeMillis();
        this.array = new int[number];
    }


    public synchronized boolean trying() {
        refresh(calculationTimes());
        // 计数
        int count = 0;
        for ( int i = 0; i < array.length; i++ ) {
            count += array[i];
        }

        if ( count >= limit ) {
            return false;
        } else {
            array[index]++;
            return true;
        }
    }

    public long calculationTimes() {
        long now = System.currentTimeMillis();
        if ( now > lastTime ) {
            int everySize = (windowInterval / number);
            return now / everySize - lastTime / everySize;
        }
        return 0;
    }

    /**
     * 计算当前请求距离上次请求滑动了多少次，则清理多少个区间的计数
     */
    private void refresh(long times) {
        if ( times == 0 ) {
            return;
        }
        times = Math.min( times, number );
        for ( int i = 0; i < times; i++ ) {
            index = (index + 1) % number;
            array[index] = 0;
        }
        lastTime = lastTime + times * (windowInterval / number);
    }

}
