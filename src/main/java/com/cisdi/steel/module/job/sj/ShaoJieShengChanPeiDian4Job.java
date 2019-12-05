package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.ShaoJieShengChanPeiDianExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShaoJieShengChanPeiDian4Job extends AbstractExportJob {

    @Autowired
    private ShaoJieShengChanPeiDianExecute shaoJieShengChanPeiDianExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_shengchanpeidian4;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shaoJieShengChanPeiDianExecute;
    }
}
