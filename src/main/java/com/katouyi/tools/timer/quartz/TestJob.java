package com.katouyi.tools.timer.quartz;

import com.alibaba.fastjson.JSON;
import org.quartz.*;

import java.util.Date;


/**
 * Scheduler 每次执行，都会根据JobDetail创建一个新的Job,这样可以规避Job并发访问的问题
 * 给Job对象使用注解 @DisallowConcurrentExecution 可以禁止并发执行
 *
 * JobDetail 每次都是新对象的话，JobDataMap也是新的，想要参数共用持久化
 * 使用 @PersistJobDataAfterExecution 持久化JobDetail中的JobDataMap
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
/**
 * job对象
 */
public class TestJob implements Job {

    /**
     * usingJobData的时候，会直接把值赋值过来
     * key如果冲突了, trigger value 覆盖 jobDetail value
     */
    private String name;
    public void setName(String name) {
        this.name = name;
    }

    // context 定时任务设置的很多内容
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 获取单独的detail的信息
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        JobKey key = jobDetail.getKey();

        // 获取trigger中的信息
        Trigger trigger = context.getTrigger();
        JobDataMap jobDataMap1 = trigger.getJobDataMap();

        // 获取jobData的合并信息
        /**
         * key如果冲突了, trigger value 覆盖 jobDetail value
         */
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        System.out.println(JSON.toJSONString(mergedJobDataMap));

        System.out.println("my job execute:" + new Date().toString());
    }
}