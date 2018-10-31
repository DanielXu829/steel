package com.cisdi.steel.module.quartz.task;

import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.mapper.QuartzMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Objects;

/**
 * quartz 任务  防止quartz执行变成了错误
 */
//@Component
@Slf4j
public class QuartzTask {


    @Autowired
    private QuartzMapper quartzMapper;

    @Autowired
    private Scheduler scheduler;

    /**
     * 55分钟执行
     */
    @Scheduled(cron = "0 55 0/1 * * ? ")
    public void resumeJobTask() {
        try {
            List<QuartzEntity> quartzEntities = quartzMapper.selectErrorRecord();
            if (Objects.isNull(quartzEntities) || quartzEntities.isEmpty()) {
                return;
            }
            for (QuartzEntity quartzEntity : quartzEntities) {
                JobKey key = new JobKey(quartzEntity.getJobName(), quartzEntity.getJobGroup());
                log.debug("resumeJob " + quartzEntity.getJobGroup() + "-" + quartzEntity.getJobName());
                scheduler.resumeJob(key);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
