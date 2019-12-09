package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.ExcelReadWriter;
import com.cisdi.steel.module.job.IExcelReadWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基本用的 执行器
 * <p>Copyright: Copyright (c) 2019</p>
 * <P>Date: 2019/12/7 </P>
 */
@Component
public class BaseGlExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ExcelReadWriter excelReadWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return excelReadWriter;
    }
}
