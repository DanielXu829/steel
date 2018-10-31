package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData6;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData7;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料车间生产运行记录表
 */
@Component
public class YuanliaoGongliaoJob7 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData7.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_chejianshengchanyunxing;
    }
}
