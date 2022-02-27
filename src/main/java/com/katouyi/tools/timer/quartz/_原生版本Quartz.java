package com.katouyi.tools.timer.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobStore;


public class _原生版本Quartz {

    public static void main(String[] args) {

        // 创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
                .withIdentity("job1", "group1")     // job的名称和分组
                //.withIdentity(name)
                //.withIdentity(JobKey)
                // .usingJobData(map)
                .usingJobData("key1", "val1")     // 给一个Map里面放数据
                .build();

        // 创建trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "triGroup1")
                //.withIdentity(name)
                //.withIdentity(TriggerKey)
                .startNow()                 // 立刻执行
                //.startAt(new Date())      // 定时开始
                // .usingJobData(map)
                .usingJobData("key1", "val2")     // 给一个Map里面放数据
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).repeatForever()) // 10s执行一次，一直执行下去
                .build();

        // 创建scheduler
        try {
            /**
             * Scheduler 每次执行，都会根据JobDetail创建一个新的Job,这样可以规避Job并发访问的问题
             * 给Job对象使用注解 @DisallowConcurrentExecution 可以禁止并发执行
             *
             * JobDetail 每次都是新对象的话，JobDataMap也是新的，想要参数共用持久化
             * 使用 @PersistJobDataAfterExecution 持久化JobDetail中的JobDataMap
             */
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }


    }
}
