package com.cisdi.steel.module.job.r3;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r3.data.TuoxiaoyunxingjiluData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:   脱硝运行记录表      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/29 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobTuoxiaoyunxingjilu extends AbstractJob {
    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(TuoxiaoyunxingjiluData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_tuoxiaoyunxingjilu;
    }
}
