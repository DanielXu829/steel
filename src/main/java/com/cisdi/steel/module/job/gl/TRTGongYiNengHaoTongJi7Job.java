package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.TRTGongYiNengHaoTongJiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRTGongYiNengHaoTongJi7Job extends AbstractExportJob {

    @Autowired
    private TRTGongYiNengHaoTongJiExecute trtGongYiNengHaoTongJiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_gongyinenghaotongJi7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return trtGongYiNengHaoTongJiExecute;
    }
}
