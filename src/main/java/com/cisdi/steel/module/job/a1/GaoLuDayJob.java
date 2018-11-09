package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.GaoLuDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉日报表
 */
@Component
public class GaoLuDayJob extends AbstractExportJob {

    @Autowired
    private GaoLuDayExecute gaoLuDayExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_jswgaolu_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gaoLuDayExecute;
    }
}
