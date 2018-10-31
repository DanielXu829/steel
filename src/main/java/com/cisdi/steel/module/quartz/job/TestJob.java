package com.cisdi.steel.module.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.io.Serializable;
import java.util.Date;

/**
 * Job 的实例要到该执行它们的时候才会实例化出来。每次 Job 被执行，一个新的 Job 实例会被创建。
 * 其中暗含的意思就是你的 Job 不必担心线程安全性，因为同一时刻仅有一个线程去执行给定 Job 类的实例，甚至是并发执行同一 Job 也是如此。
 * <p>Description: 测试的定时任务 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@Slf4j
public class TestJob implements Job, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("定时任务测试中");
        log.info("Hello!  NewJob is executing." + new Date());
        //取得job详情
        JobDetail jobDetail = context.getJobDetail();
        // 取得job名称
        String jobName = jobDetail.getClass().getName();
        log.info("Name: " + jobDetail.getClass().getSimpleName());
        //取得job的类
        log.info("Job Class: " + jobDetail.getJobClass());
        //取得job开始时间
        log.info(jobName + " fired at " + context.getFireTime());
        //取得job下次触发时间
        log.info("Next fire time " + context.getNextFireTime());
    }
}
