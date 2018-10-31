package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData7;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData8;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料车间生产交班表
 */
@Component
public class YuanliaoGongliaoJob8 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData8.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_chejianshengchanjiaoban;
    }
}
