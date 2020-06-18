package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.LuDiWenDuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LuDiWenDuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LuDiWenDuWriter luDiWenDuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luDiWenDuWriter;
    }
}
