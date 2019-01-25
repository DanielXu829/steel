package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.ChutiezonglanExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezonglanJob extends AbstractExportJob {

    @Autowired
    private ChutiezonglanExecute chutiezonglanExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chutiezonglan;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return chutiezonglanExecute;
    }
}
