package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.CK12PeimeiliangWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ck12配煤量月报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class CK12PeimeiliangExecute extends AbstractJobExecuteExecute {

    @Autowired
    private CK12PeimeiliangWriter peimeiliangWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return peimeiliangWriter;
    }

}
