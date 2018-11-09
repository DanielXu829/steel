package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.a5.writer.BaseNjWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TwokongDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BaseNjWriter baseNjWriter;

    @Override
    public void initConfig() {
        this.excelWriter = baseNjWriter;
    }
}
