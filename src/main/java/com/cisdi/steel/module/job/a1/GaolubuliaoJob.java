package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.GaolubuliaoExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 8高炉布料
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaolubuliaoJob  extends AbstractExportJob {

    @Autowired
    private GaolubuliaoExecute gaolubuliaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_gaolubuliao;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gaolubuliaoExecute;
    }
}
