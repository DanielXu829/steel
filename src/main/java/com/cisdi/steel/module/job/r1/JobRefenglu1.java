package com.cisdi.steel.module.job.r1;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r1.data.RefengluData1;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:    热风炉 日报     </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/25 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobRefenglu1 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(RefengluData1.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.refenglu_day;
    }
}
