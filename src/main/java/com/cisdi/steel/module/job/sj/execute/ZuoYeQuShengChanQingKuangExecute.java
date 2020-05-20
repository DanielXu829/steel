package com.cisdi.steel.module.job.sj.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.sj.writer.ZuoYeQuShengChanQingKuangWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZuoYeQuShengChanQingKuangExecute extends AbstractJobExecuteExecute {
    @Autowired
    public ZuoYeQuShengChanQingKuangWriter zuoYeQuShengChanQingKuangWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zuoYeQuShengChanQingKuangWriter;
    }
}
