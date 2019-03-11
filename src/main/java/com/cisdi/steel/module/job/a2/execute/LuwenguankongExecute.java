package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.FensuixiduWriter;
import com.cisdi.steel.module.job.a2.writer.LuwenguankongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LuwenguankongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LuwenguankongWriter luwenguankongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return luwenguankongWriter;
    }
}
