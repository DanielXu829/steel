package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.LugangWenduDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉炉缸冷却壁进出水日报
 */
@Component
public class GaoLuluganglengquebiDayJob extends AbstractExportJob {

    @Autowired
    private LugangWenduDayExecute lugangWenduDayExecute;
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lglqbjcs_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return lugangWenduDayExecute;
    }

}
