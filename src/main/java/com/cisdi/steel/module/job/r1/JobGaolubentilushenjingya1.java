package com.cisdi.steel.module.job.r1;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r1.data.GaolubentilushenjingyaData1;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  高炉本体炉身静压 日报 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobGaolubentilushenjingya1 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(GaolubentilushenjingyaData1.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gaolubentilushenjingya_day;
    }
}
