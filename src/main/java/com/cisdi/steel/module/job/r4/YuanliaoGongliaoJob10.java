package com.cisdi.steel.module.job.r4;

import com.cisdi.steel.common.util.ApplicationContextHolder;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData10;
import com.cisdi.steel.module.data.r4.data.YuanliaoGongliaoData9;
import com.cisdi.steel.module.job.AbstractJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 原料混匀矿粉A4干转湿配比换算计算表
 */
@Component
public class YuanliaoGongliaoJob10 extends AbstractJob {

    @Override
    protected void init() {
        this.dataHandler = ApplicationContextHolder.getBean(YuanliaoGongliaoData10.class);
    }

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.yl_hunyunkuangfenA4ganzhuanshi;
    }
}
