package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.DianLiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 电力
 */
@Component
public class DianLiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private DianLiWriter dianLiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return dianLiWriter;
    }
}
