package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.BaseCommonGlExecute;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 高炉通用的处理
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractBaseCommonExportJob1 extends AbstractExportJob {

    @Autowired
    private BaseCommonGlExecute baseCommonGlExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return baseCommonGlExecute;
    }
}
