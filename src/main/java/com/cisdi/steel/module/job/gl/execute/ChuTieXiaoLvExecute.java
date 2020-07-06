package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.ChuTieHuaXueChengFenWriter;
import com.cisdi.steel.module.job.gl.writer.ChuTieXiaoLvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChuTieXiaoLvExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ChuTieXiaoLvWriter writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return writer;
    }
}
