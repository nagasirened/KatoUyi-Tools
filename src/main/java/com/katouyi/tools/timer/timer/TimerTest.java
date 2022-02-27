package com.katouyi.tools.timer.timer;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {

    public static void main(String[] args) {
        /**
         * 第一步，就创建了一个TimerThread线程，并启动了
         * TimerThread中存储了一个TaskQueue(小顶堆),不断轮询去获取任务处理，没有任务TaskQueue就wait，（queue.getMin() == task）有任务就notify唤醒
         */
        Timer timer = new Timer();

        /**
         * 添加任务后，会唤醒queue； Task重新拿离当前时间最近的任务，如果还是没有达到触发条件，就继续wait(minTime - currentTime)
         */

        // 提交一个延时任务，仅执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("测试");
            }
        }, 1000);


        /**
         * 普通的schedule提交, 作为循环任务，在上一个任务完成之后，重新添加任务是，根据period时间间隔重新设置时间   schedule方法period传的是负数
         * 如果任务执行1s一次，period设置2s, 执行时间就应该是0s开始，3s开始，6s开始
         *
         * timer.scheduleAtFixedRate();  直接比较任务应该的执行时间是否小于currentTime,如果小于，则直接执行   scheduleAtFixedRate方法period传的是正数
         *
         *
         * currentTime = System.currentTimeMillis();
         *                         executionTime = task.nextExecutionTime;
         *                         if (taskFired = (executionTime<=currentTime)) {
         *                             if (task.period == 0) { // Non-repeating, remove
         *                                 queue.removeMin();
         *                                 task.state = TimerTask.EXECUTED;
         *                             } else { // Repeating task, reschedule
         *                                 queue.rescheduleMin(
         *                                   task.period<0 ? currentTime   - task.period                ## 就是这里，我日牛皮
         *                                                 : executionTime + task.period);              ## 我日牛逼
         *                             }
         *                         }
         */

    }
}
