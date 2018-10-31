package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData4;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料堆混匀矿粉配比通知单 任务
 */
@Component
public class YuanliaoGongliaoJob4 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData4.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_duihunyunkuangfenpeibi;
    }
}
