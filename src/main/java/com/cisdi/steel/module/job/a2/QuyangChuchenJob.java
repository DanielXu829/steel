package com.cisdi.steel.module.job.a2;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a2.execute.BaseChuChenExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class QuyangChuchenJob extends AbstractExportJob {

    @Autowired
    private BaseChuChenExecute baseJhExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_quyangchuchen;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return baseJhExecute;
    }
}
