package com.cisdi.steel.module.quartz.controller;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.query.CronQuery;
import com.cisdi.steel.module.quartz.query.QuartzEntityQuery;
import com.cisdi.steel.module.quartz.service.JobService;
import com.cisdi.steel.module.quartz.util.QuartzUtil;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * <p>Description: 定时器任务 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@RestController
@RequestMapping("/quartz/job")
@Slf4j
public class JobController {

    private final Scheduler scheduler;
    private final JobService jobService;
    private final SysConfigService sysConfigService;

    @Autowired
    public JobController(Scheduler scheduler, JobService jobService, SysConfigService sysConfigService, ReportCategoryTemplateService reportCategoryTemplateService) {
        this.scheduler = scheduler;
        this.jobService = jobService;
        this.sysConfigService = sysConfigService;
        this.reportCategoryTemplateService = reportCategoryTemplateService;
    }

    private final ReportCategoryTemplateService reportCategoryTemplateService;
    /**
     * 新建任务或更新任务
     */
    @PostMapping("/save")
    public ApiResult save(@RequestBody QuartzEntity quartz) {
        log.info("更新任务");
        try {
            //如果是修改  展示旧的 任务
            if (quartz.getJobName() != null) {
                JobKey key = new JobKey(quartz.getJobName(), quartz.getJobGroup());
                // 删除旧的任务
                scheduler.deleteJob(key);
            } else {
                return ApiUtil.fail("编码不能为空");
            }

            //通过任务编码获取执行类
            String action = sysConfigService.selectActionByCode(quartz.getJobName());
//            Class cls = Class.forName(quartz.getJobClassName());
            Class cls = Class.forName(action);
            cls.newInstance();
            //构建job信息
            JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
                    quartz.getJobGroup())
                    .withDescription(quartz.getDescription())
                    .build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + quartz.getJobName(), quartz.getJobGroup())
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("修改任务报错", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }

    @PostMapping("/saveInfo")
    public ApiResult saveInfo(@RequestBody QuartzEntity quartz) {
        log.info("更新任务");
        try {
            reportCategoryTemplateService.updateTemplateTask(quartz);
            //如果是修改  展示旧的 任务
            if (quartz.getJobName() != null) {
                JobKey key = new JobKey(quartz.getJobName(), quartz.getJobGroup());
                // 删除旧的任务
                scheduler.deleteJob(key);
            } else {
                return ApiUtil.fail("编码不能为空");
            }

            //通过任务编码获取执行类
            String action = sysConfigService.selectActionByCode(quartz.getJobName());
//            Class cls = Class.forName(quartz.getJobClassName());
            Class cls = Class.forName(action);
            cls.newInstance();
            //构建job信息
            JobDetail job = JobBuilder.newJob(cls).withIdentity(quartz.getJobName(),
                    quartz.getJobGroup())
                    .withDescription(quartz.getDescription())
                    .build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + quartz.getJobName(), quartz.getJobGroup())
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("修改任务报错", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }

    /**
     * 计算cron表达式
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping(value = "/cronSchedule")
    public ApiResult cronSchedule(@RequestBody CronQuery query) {
        if (StringUtils.isBlank(query.getCron())) {
            return ApiUtil.fail("cron表达式不能为空");
        }
        List<String> cronSchedule = QuartzUtil.getCronSchedule(query.getCron(), query.getCount());
        if (Objects.isNull(cronSchedule)) {
            return ApiUtil.fail("cron表达式错误");
        } else {
            return ApiUtil.success(cronSchedule);
        }
    }

    /**
     * 任务列表
     */
    @PostMapping("/list")
    public ApiResult list(@RequestBody QuartzEntityQuery query) {
        log.info("任务列表");
        return jobService.listQuartzEntity(query);
    }

    /**
     * 查询所有分组
     */
    @PostMapping("/listGroups")
    public ApiResult list() {
        return jobService.selectAllGroupName();
    }

    /**
     * 触发任务（执行对应的任务）
     */
    @PostMapping("/trigger")
    public ApiResult trigger(@RequestBody QuartzEntity quartz) {
        log.info("触发任务");
        try {
            JobKey key = new JobKey(quartz.getJobName(), quartz.getJobGroup());
            scheduler.triggerJob(key);
        } catch (SchedulerException e) {
            log.error("触发任务报错", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }

    /**
     * 暂停任务
     */
    @PostMapping("/pause")
    public ApiResult pause(@RequestBody QuartzEntity quartz) {
        log.info("停止任务");
        try {
            JobKey key = new JobKey(quartz.getJobName(), quartz.getJobGroup());
            scheduler.pauseJob(key);
        } catch (SchedulerException e) {
            log.error("停止任务异常", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }

    /**
     * 恢复任务
     */
    @PostMapping("/resume")
    public ApiResult resume(@RequestBody QuartzEntity quartz) {
        log.info("恢复任务");
        try {
            JobKey key = new JobKey(quartz.getJobName(), quartz.getJobGroup());
            scheduler.resumeJob(key);
        } catch (SchedulerException e) {
            log.error("恢复任务", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }

    /**
     * 移除任务
     */
    @PostMapping("/remove")
    public ApiResult remove(@RequestBody QuartzEntity quartz) {
        log.info("移除任务");
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(quartz.getJobName(), quartz.getJobGroup());
            // 停止触发器  
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器  
            scheduler.unscheduleJob(triggerKey);
            // 删除任务  
            scheduler.deleteJob(JobKey.jobKey(quartz.getJobName(), quartz.getJobGroup()));
            log.info("removeJob:" + JobKey.jobKey(quartz.getJobName()));
        } catch (Exception e) {
            log.error("移除任务", e);
            return ApiUtil.fail();
        }
        return ApiUtil.success();
    }
}
