package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.LuWenHeGeLvWriter;
import com.cisdi.steel.module.job.gl.writer.LuZhaJianDuHeGeLvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LuZhaJianDuHeGeLvExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LuZhaJianDuHeGeLvWriter writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return writer;
    }
}
