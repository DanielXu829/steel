package com.cisdi.steel.module.job;

import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import com.cisdi.steel.module.job.enums.JobEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
public abstract class AbstractExportJob implements Job, Serializable {

    /**
     * 获取当前的 工作类型
     *
     * @return 获取当前的类型 不能为null
     */
    public abstract JobEnum getCurrentJob();

    /**
     * 获取当前执行的实例
     *
     * @return 对应的执行器
     */
    public abstract IJobExecute getCurrentJobExecute();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        IJobExecute jobHandler = getCurrentJobExecute();
        Objects.requireNonNull(jobHandler, "没有执行的类");
        try {
            log.debug(getCurrentJob().getName() + "       开始执行");
            // 如果jobExecutionContext中report category code不为空，则修改报表的job code为此值
            JobEnum currentJob = getCurrentJob();
            if (Objects.nonNull(jobExecutionContext)) {
                JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
                String reportCategoryCode = (String) mergedJobDataMap.get("report_category_code");
                if (StringUtils.isNotBlank(reportCategoryCode)) {
                    currentJob.setCode(reportCategoryCode);
                }
            }
            JobExecuteInfo jobExecuteInfo = JobExecuteInfo.builder()
                    .jobEnum(currentJob)
                    .build();
            jobHandler.execute(jobExecuteInfo);
            log.debug(getCurrentJob().getName() + "       执行结束");
        } catch (Exception e) {
            log.error(getCurrentJob().getName() + "       执行错误", e);
        }
    }
}
