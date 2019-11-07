package com.cisdi.steel.module.job.jh.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.jh.writer.GxjShenCanWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 干熄焦生产处理器 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/05 </P>
 *
 * @version 1.0
 */
@Component
public class GxjShenCanExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GxjShenCanWriter gxjShenCanWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gxjShenCanWriter;
    }
}
