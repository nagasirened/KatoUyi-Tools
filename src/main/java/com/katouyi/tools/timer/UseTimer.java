package com.katouyi.tools.timer;

import java.util.Timer;
import java.util.TimerTask;

public class UseTimer {

    public static void main(String[] args) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("threadName：" + Thread.currentThread().getName() + "，current ms is " + System.currentTimeMillis());
            }
        };

        System.out.println("threadName：" + Thread.currentThread().getName() + "，current ms is " + System.currentTimeMillis());
        // isDaemon: true  代表是守护进程，主线程运行完了之后，没有别的线程了，timer不会执行
        // isDaemon: false 会让程序一致运行
        // 缺点：一个timer只能串行执行它的任务
        Timer timer = new Timer("timer", false);

        /*
         * delay 代表第一次执行的延后时间
         * period代表后续间隔执行时间
         */
        int delay = 2000;
        int period = 3000;
        timer.schedule(task, delay, period);
    }
}
