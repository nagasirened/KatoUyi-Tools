package com.katouyi.tools.timer.quartz.spring;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 监听器，Spring容器启动，自动开始任务执行
 */
@Component
public class StartApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        TriggerKey triggerKey = TriggerKey.triggerKey("trigger1", "group1");
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (Objects.isNull(trigger)) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                        .build();
            }

            JobDetail jobDetail = JobBuilder.newJob(CustomQuartzJob.class)
                    .withIdentity("custom_job", "job_group_v1")
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
