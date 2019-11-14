package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.BianLiaoJiLuExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#变料记录表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/14 </P>
 *
 * @version 1.0
 */
@Component
public class BianLiaoJiLu8Job extends AbstractExportJob {

    @Autowired
    private BianLiaoJiLuExecute bianLiaoJiLu8Execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_bianliaojilu8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return bianLiaoJiLu8Execute;
    }
}
