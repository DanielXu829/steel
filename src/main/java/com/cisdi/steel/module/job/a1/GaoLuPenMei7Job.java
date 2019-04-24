package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.GaoLuPenMeiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 7高炉喷煤月报表
 */
@Component
public class GaoLuPenMei7Job extends AbstractExportJob {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_gaolupenmei7;
    }

    @Autowired
    private GaoLuPenMeiExecute penMeiExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return penMeiExecute;
    }

}
