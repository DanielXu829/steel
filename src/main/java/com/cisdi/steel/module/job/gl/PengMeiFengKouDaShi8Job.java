package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.PengMeiFengKouDaShiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#喷煤风口报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/13 </P>
 *
 * @version 1.0
 */
@Component
public class PengMeiFengKouDaShi8Job extends AbstractExportJob {

    @Autowired
    private PengMeiFengKouDaShiExecute pengMeiFengKouDaShiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_pengmeifengkoudashi8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return pengMeiFengKouDaShiExecute;
    }
}
