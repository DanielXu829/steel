package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.BF6gongyicanshuExecute;
import com.cisdi.steel.module.job.a1.execute.GuankongzhibiaoExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉日报表
 */
@Component
public class BF6gongyicanshuJob extends AbstractExportJob {

    @Autowired
    private BF6gongyicanshuExecute bf6gongyicanshuExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_bf6gongyicanshu;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return bf6gongyicanshuExecute;
    }
}
