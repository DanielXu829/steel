package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.MeiZhouChanLiangFenXiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeiZhouChanLiangFenXiJob extends AbstractExportJob {

    @Autowired
    private MeiZhouChanLiangFenXiExecute meiZhouChanLiangFenXiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_meizhouchanliangfenxi;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return meiZhouChanLiangFenXiExecute;
    }
}
