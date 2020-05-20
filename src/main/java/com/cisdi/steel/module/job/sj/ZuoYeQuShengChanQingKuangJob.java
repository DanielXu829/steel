package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.ZuoYeQuShengChanQingKuangExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZuoYeQuShengChanQingKuangJob extends AbstractExportJob {
    @Autowired
    private ZuoYeQuShengChanQingKuangExecute zuoYeQuShengChanQingKuangExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_zuoyequshengchanqingkuang;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return zuoYeQuShengChanQingKuangExecute;
    }
}
