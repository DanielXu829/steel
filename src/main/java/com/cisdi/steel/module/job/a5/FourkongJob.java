package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.AcsExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 四空压站运行记录表
 */
@Component
public class FourkongJob extends AbstractBaseCommonExportJob5 {
    @Autowired
    private AcsExecute acsExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return acsExecute;
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_fourkong;
    }
}
