package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.RefenglujiankongExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefenglujiankongDayJob extends AbstractExportJob {

    @Autowired
    private RefenglujiankongExecute refenglujiankongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_refenglujiankong;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return refenglujiankongExecute;
    }
}
