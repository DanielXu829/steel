package com.cisdi.steel.module.job.a2;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a2.execute.CK12PeimeiliangExecute;
import com.cisdi.steel.module.job.a2.execute.CK45PeimeiliangExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ck12配煤量月报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class CK12PeimeiliangJob extends AbstractExportJob {

    @Autowired
    private CK12PeimeiliangExecute peimeiliangExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_ck12peimeiliang;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return peimeiliangExecute;
    }
}
