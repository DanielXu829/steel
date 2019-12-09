package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.BaseCommonGlExecute;
import com.cisdi.steel.module.job.gl.execute.BaseGlExecute;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 高炉通用的处理
 * <p>Copyright: Copyright (c) 2019</p>
 * <P>Date: 2019/12/7 </P>
 */
public abstract class AbstractBaseExportJob extends AbstractExportJob {

    @Autowired
    private BaseGlExecute baseGlExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return baseGlExecute;
    }
}
