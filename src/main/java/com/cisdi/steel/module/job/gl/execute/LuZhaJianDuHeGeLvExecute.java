package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.JingYiTongJiBiaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LuZhaJianDuHeGeLvExecute extends AbstractJobExecuteExecute {

    @Autowired
    private JingYiTongJiBiaoWriter writer;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return writer;
    }
}
