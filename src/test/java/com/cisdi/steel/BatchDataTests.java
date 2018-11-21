package com.cisdi.steel;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.sys.entity.SysConfig;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
public class BatchDataTests extends SteelApplicationTests {

    @Autowired
    private SysConfigService sysConfigService;


    @Autowired
    private Scheduler scheduler;

    private static final String jobGroup = "所有";

    /**
     * 批量插入 所有job
     * 名称 编码 对应类的class  插入到sysConfig表中
     */
    @Test
    public void test1() {
        List<SysConfig> all = getAll();
        sysConfigService.saveBatch(all);
    }


    /**
     * 开启所有任务
     * 0 0/10 * * * ? 每10分钟
     */
    @Test
    public void test2() {
        List<SysConfig> all = getAll();
        for (SysConfig sysConfig : all) {
            createTask(sysConfig.getCode(), jobGroup, "0 0 0/4 * * ?", "");
        }
    }

    /**
     * 移除所有任务
     */
    @Test
    public void test3() {
        List<SysConfig> all = getAll();
        for (SysConfig sysConfig : all) {
            JobKey jobKey = JobKey.jobKey(sysConfig.getCode(), jobGroup);
            try {
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取所有的类
     *
     * @return
     */
    private List<SysConfig> getAll() {
        Map<String, Job> beansOfType = ApplicationContextHolder.getApplicationContext().getBeansOfType(Job.class);
        List<SysConfig> sysConfigs = new ArrayList<>();
        beansOfType.forEach((k, v) -> {
            String name = v.getClass().getPackage().getName();
            // 查找a开头的包里面的类
            if (name.startsWith("com.cisdi.steel.module.job.a")) {
                AbstractExportJob abstractExportJob = (AbstractExportJob) v;
                SysConfig t = new SysConfig();
                t.setCode(abstractExportJob.getCurrentJob().getCode());
                t.setName(abstractExportJob.getCurrentJob().getName());
                t.setAction(v.getClass().getName());
                sysConfigs.add(t);
            }
        });
        return sysConfigs;
    }

    private void createTask(String jobName, String jobGroup, String cronExpression, String desc) {
        try {
            //通过任务编码获取执行类
            String action = sysConfigService.selectActionByCode(jobName);
//            Class cls = Class.forName(quartz.getJobClassName());
            Class cls = Class.forName(action);
            cls.newInstance();
            //构建job信息
            JobDetail job = JobBuilder.newJob(cls).withIdentity(jobName,
                    jobGroup)
                    .withDescription(desc)
                    .build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger" + jobName, jobGroup)
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //交由Scheduler安排触发
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            log.error("新建任务报错", e);
        }
    }
}