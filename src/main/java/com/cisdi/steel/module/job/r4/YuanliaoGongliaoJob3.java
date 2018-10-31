package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData3;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 供料异常跟踪表
 */
@Component
public class YuanliaoGongliaoJob3 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData3.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_yichanggenzong;
    }
}
