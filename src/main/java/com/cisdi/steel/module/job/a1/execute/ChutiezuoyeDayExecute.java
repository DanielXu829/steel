package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.a1.readWriter.ChutiezuoyeDayReadWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/8 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezuoyeDayExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ChutiezuoyeDayReadWriter chutiezuoyeDayReadWriter;

    @Override
    public void initConfig() {
        this.excelWriter = chutiezuoyeDayReadWriter;
    }
}
