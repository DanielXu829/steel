package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.GaolubuliaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉布料
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaolubuliaoExecute extends AbstractJobExecuteExecute {

    @Autowired
    private GaolubuliaoWriter gaolubuliaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return gaolubuliaoWriter;
    }
}
