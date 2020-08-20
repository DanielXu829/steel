package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.MeiZhouChanLiangFenXiExecute;
import com.cisdi.steel.module.job.sj.execute.ShengChanFenXiZhouBaoExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShengChanFenXiZhouBaoJob extends AbstractExportJob {

    @Autowired
    private ShengChanFenXiZhouBaoExecute shengChanFenXiZhouBaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_shengchanfenxizhoubao;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shengChanFenXiZhouBaoExecute;
    }
}
