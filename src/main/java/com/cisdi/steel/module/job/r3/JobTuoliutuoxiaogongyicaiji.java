package com.cisdi.steel.module.job.r3;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r3.data.TuoliutuoxiaogongyicaijiData;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:  脱硫脱硝工艺参数采集       </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/30 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JobTuoliutuoxiaogongyicaiji extends AbstractJob {
    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(TuoliutuoxiaogongyicaijiData.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_tuoliutuoxiaogongyicaiji;
    }
}
