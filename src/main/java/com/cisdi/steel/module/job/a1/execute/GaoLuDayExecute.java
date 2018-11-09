package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.cisdi.steel.module.job.a1.readwriter.BaseReadWriter;


/**
 * 高炉日报表 执行的类
 */
@Component
@Slf4j
public class GaoLuDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseReadWriter writerExcel;

    @Override
    public void initConfig() {
        this.excelWriter = writerExcel;
    }
}
