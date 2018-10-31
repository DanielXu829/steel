package com.cisdi.steel.module.job.r2;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r2.data.PeimeizuoyequData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  配煤作业区报表设计       </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/26 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobPeimeizuoyequ extends AbstractJob {
    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(PeimeizuoyequData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_peimeizuoyequ;
    }
}
