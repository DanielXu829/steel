package com.cisdi.steel.module.job.jh.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.jh.writer.ChuChenWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChuChenExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ChuChenWriter chuChenWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return chuChenWriter;
    }
}
