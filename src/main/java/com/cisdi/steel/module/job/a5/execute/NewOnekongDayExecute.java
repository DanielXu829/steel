package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.a5.writer.BaseNjWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 新一空压站运行记录表
 */
@Component
public class NewOnekongDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseNjWriter baseNjWriter;

    @Override
    public void initConfig() {
        this.excelWriter = baseNjWriter;
    }
}
