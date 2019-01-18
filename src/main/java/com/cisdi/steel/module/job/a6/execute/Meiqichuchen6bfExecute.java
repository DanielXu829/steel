package com.cisdi.steel.module.job.a6.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.BaseJhWriter;
import com.cisdi.steel.module.job.a6.writer.Meiqichuchen6bfWriter;
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
public class Meiqichuchen6bfExecute extends AbstractJobExecuteExecute {

    @Autowired
    private Meiqichuchen6bfWriter meiqichuchen6bfWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return meiqichuchen6bfWriter;
    }
}
