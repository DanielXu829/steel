package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.TRTGongYiNengHaoTongJiExecute;
import com.cisdi.steel.module.job.gl.execute.YueBaoHuiZongExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRTGongYiNengHaoTongJi8Job extends AbstractExportJob {

    @Autowired
    private TRTGongYiNengHaoTongJiExecute trtGongYiNengHaoTongJiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_gongyinenghaotongJi8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return trtGongYiNengHaoTongJiExecute;
    }
}
