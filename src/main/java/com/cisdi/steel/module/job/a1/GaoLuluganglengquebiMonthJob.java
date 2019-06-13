package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.LugangWenduDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉冷却壁水温差月报
 */
@Component
public class GaoLuluganglengquebiMonthJob extends AbstractExportJob {

    @Autowired
    private LugangWenduDayExecute lugangWenduDayExecute;
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lglqbjcs_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return lugangWenduDayExecute;
    }

}
