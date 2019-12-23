package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.ShaoJieShengChanExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShaoJieShengChan4Job extends AbstractExportJob {

    @Autowired
    private ShaoJieShengChanExecute shaoJieShengChanExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_shengchan4;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shaoJieShengChanExecute;
    }
}
