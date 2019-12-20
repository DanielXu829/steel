package com.cisdi.steel.module.job.jh;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.jh.execute.ChuChenExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GxjChuChenJob extends AbstractExportJob {

    @Autowired
    private ChuChenExecute chuChenExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_gxjchuchen;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return chuChenExecute;
    }
}
