package com.cisdi.steel.module.job.sj;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.sj.execute.DuiChangYunXingTongJiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuiChangYunXingTongJiJob extends AbstractExportJob {
    @Autowired
    private DuiChangYunXingTongJiExecute duiChangYunXingTongJiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_duichangyunxingtongji;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return duiChangYunXingTongJiExecute;
    }
}
