package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.LuWenHeGeLvExecute;
import com.cisdi.steel.module.job.gl.execute.TieShuiYiJiPinLvExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TieShuiYiJiPinLv8Job extends AbstractExportJob {

    @Autowired
    private TieShuiYiJiPinLvExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_tieshuiyijipinlv;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
