package com.cisdi.steel.module.job.r3;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r3.data.TuoliuData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:    脱硫     </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/1 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobTuoliu extends AbstractJob {
    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(TuoliuData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_tuoliu;
    }
}
