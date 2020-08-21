package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.TRTGongYiNengHaoTongJiExecute;
import com.cisdi.steel.module.job.gl.execute.TRTJiXieJianKongExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRTJiXieJianKong8Job extends AbstractExportJob {

    @Autowired
    private TRTJiXieJianKongExecute trtJiXieJianKongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_jixiejiankong8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return trtJiXieJianKongExecute;
    }
}
