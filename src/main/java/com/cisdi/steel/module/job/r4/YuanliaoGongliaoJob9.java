package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData8;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData9;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料数据记录表
 */
@Component
public class YuanliaoGongliaoJob9 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData9.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_shujujilu;
    }
}
