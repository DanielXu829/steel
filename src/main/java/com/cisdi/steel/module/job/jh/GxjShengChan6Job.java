package com.cisdi.steel.module.job.jh;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.jh.execute.GxjShengChanExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 6#干熄焦生产报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/05 </P>
 *
 * @version 1.0
 */
@Component
public class GxjShengChan6Job extends AbstractExportJob {

    @Autowired
    private GxjShengChanExecute gxjShengChanExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_gxjshengchan6;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gxjShengChanExecute;
    }
}
