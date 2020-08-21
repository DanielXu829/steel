package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.TRTJiXieJianKongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TRTJiXieJianKongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private TRTJiXieJianKongWriter trtJiXieJianKongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return trtJiXieJianKongWriter;
    }
}
