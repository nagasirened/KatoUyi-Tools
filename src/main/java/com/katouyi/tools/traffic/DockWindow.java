package com.katouyi.tools.traffic;


public class DockWindow {

    public long timestamp;
    /* 计数器 */
    public int counter;
    /* 消息的速度限制, 大于0的整数 */
    public final int limit;
    /* 时间窗口为1000ms */
    public final int window;

    public DockWindow(int limit, int window) {
        this.limit = limit;
        this.window = window;
        this.timestamp = System.currentTimeMillis();
        this.counter = 0;
    }

    public boolean grant() {
        long now = System.currentTimeMillis();
        if ( now < timestamp + window ) {
            return ++counter <= limit;
        } else {
            timestamp = now;
            counter = 1;
            return true;
        }
    }
}
