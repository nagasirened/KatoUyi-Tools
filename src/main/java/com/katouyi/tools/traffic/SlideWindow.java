package com.katouyi.tools.traffic;

public class SlideWindow {

    /* 记录上一次请求的时间 */
    public long lastTime = System.currentTimeMillis();
    /* 记录时间窗时间大小, 单位ms */
    public final int interval = 1000;
    /* 区间数量 */
    public final int number = 10;
    /* 流量限制 */
    public final int limit = 5;
    /* counter, 区间数为10 */
    public final int[] array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    /* 当前处理窗口的下标 */
    public int index = 0;

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
            int everySize = (interval / number);
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
        lastTime = lastTime + times * (interval / number);
    }

}
