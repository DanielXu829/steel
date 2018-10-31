package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData5;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料进厂物资精煤化验记录表 任务
 */
@Component
public class YuanliaoGongliaoJob5 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData5.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_jinchangwuzijingmeihuayan;
    }
}
