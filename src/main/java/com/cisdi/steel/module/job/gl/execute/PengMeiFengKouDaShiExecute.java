package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.PengMeiFengKouDaShiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 喷煤风口执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/13 </P>
 *
 * @version 1.0
 */
@Component
public class PengMeiFengKouDaShiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private PengMeiFengKouDaShiWriter pengMeiFengKouDaShiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return pengMeiFengKouDaShiWriter;
    }
}
