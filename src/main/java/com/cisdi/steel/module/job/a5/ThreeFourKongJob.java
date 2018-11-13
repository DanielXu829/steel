package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.ThreeFourKongExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ThreeFourKongJob extends AbstractExportJob {

    @Autowired
    private ThreeFourKongExecute threeFourKongExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_sansigui_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return threeFourKongExecute;
    }
}
