package com.cisdi.steel.module.job.drt;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DynamicReportJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();
        String reportCategoryCode = (String) mergedJobDataMap.get("report_category_code");
    }
}
