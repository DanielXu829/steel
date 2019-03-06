package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.RefenglujiankongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefenglujiankongExecute  extends AbstractJobExecuteExecute {

    @Autowired
    private RefenglujiankongWriter refenglujiankongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return refenglujiankongWriter;
    }
}
