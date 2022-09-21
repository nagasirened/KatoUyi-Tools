package com.katouyi.tools.traffic;

import java.util.concurrent.atomic.AtomicInteger;

public class DockWindow {

    public long timestamp = System.currentTimeMillis();
    /* 计数器 */
    public AtomicInteger counter = new AtomicInteger(0);
    /* 消息的速度限制, 大于0的整数 */
    public final int limit = 5;
    /* 时间窗口为1000ms */
    public final int window = 1000;

    public boolean grant() {
        long now = System.currentTimeMillis();
        if ( now < timestamp + window ) {
            int i = counter.addAndGet( 1 );
            return i <= limit;
        } else {
            timestamp = now;
            counter = new AtomicInteger(1);
            return true;
        }
    }
}
