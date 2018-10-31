package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData2;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 供料车间集控中心交接班记录
 */
@Component
public class YuanliaoGongliaoJob2 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData2.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chejianjikongzhongxinjioajieban;
    }
}
