package com.cisdi.steel.module.job.r1;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r1.data.ChutiezuoyeData1;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * <p>Description:  出铁作业  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Slf4j
@Component
public class ChutiezuoyeJob1 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(ChutiezuoyeData1.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.chutiezuoye_day;
    }
}
