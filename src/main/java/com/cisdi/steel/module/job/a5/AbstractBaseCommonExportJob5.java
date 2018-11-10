package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.BaseCommonNjExecute;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
public abstract class AbstractBaseCommonExportJob5 extends AbstractExportJob {

    @Autowired
    private BaseCommonNjExecute baseCommonNjExecute;

    @Override
    public IJobExecute getCurrentJobExecute() {
        return baseCommonNjExecute;
    }
}
