package com.cisdi.steel.module.job.r1;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r1.data.GaoluludingzhuangliaozuoyeData2;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:    高炉炉顶装料作业 日报1    </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaoluludingzhuangliaozuoyeJob2 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(GaoluludingzhuangliaozuoyeData2.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_ludingzhuangliaozuoye_day2;
    }
}
