package com.cisdi.steel.module.job.drt;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.drt.execute.DynamicReportTemplateExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.KaoHeYueBaoExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 动态报表 Job</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2020/1/6 </P>
 *
 * @version 1.0
 */
@Component
public class DynamicReportTemplateJob extends AbstractExportJob {

    @Autowired
    private DynamicReportTemplateExecute dynamicReportTemplateExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.drt;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return dynamicReportTemplateExecute;
    }
}
