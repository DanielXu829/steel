package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.LuZhaJianDuHeGeLvExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LuZhaJianDuHeGeLv7Job extends AbstractExportJob {

    @Autowired
    private LuZhaJianDuHeGeLvExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_luzhajianduhegelv7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
