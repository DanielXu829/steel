package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.GaoLuDayExecute;
import com.cisdi.steel.module.job.a1.execute.GaoLuMonthExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉月报表
 */
@Component
public class GaoLuMonthJob extends AbstractExportJob {

    @Autowired
    private GaoLuMonthExecute gaoLuMonthExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_taisu1_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gaoLuMonthExecute;
    }
}
