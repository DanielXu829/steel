package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.a1.readwriter.BaseReadWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 高炉月报表 执行的类
 */
@Component
@Slf4j
public class GaoLuMonthExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseReadWriter writerExcel;

    @Override
    public void initConfig() {
        this.excelWriter = writerExcel;
    }
}
