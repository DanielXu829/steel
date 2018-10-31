package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData1;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 供料车间物料外排统计 作业
 */
@Component
public class YuanliaoGongliaoJob1 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData1.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chejianwuliaowaipai;
    }
}
