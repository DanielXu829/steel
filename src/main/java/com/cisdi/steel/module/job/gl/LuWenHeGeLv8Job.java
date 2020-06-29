package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.GuiLiuShuangMingZhongLvExecute;
import com.cisdi.steel.module.job.gl.execute.LuWenHeGeLvExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LuWenHeGeLv8Job extends AbstractExportJob {

    @Autowired
    private LuWenHeGeLvExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_luwenhegelv;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
