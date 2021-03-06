package com.cisdi.steel.module.job.sj.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.sj.writer.DuiChangYunXingTongJiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DuiChangYunXingTongJiExecute extends AbstractJobExecuteExecute {
    @Autowired
    public DuiChangYunXingTongJiWriter duiChangYunXingTongJiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return duiChangYunXingTongJiWriter;
    }
}
