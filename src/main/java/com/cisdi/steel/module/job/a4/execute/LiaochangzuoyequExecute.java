package com.cisdi.steel.module.job.a4.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a4.writer.ChengPinCangWriter;
import com.cisdi.steel.module.job.a4.writer.LiaochangzuoyequWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 料场作业区
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LiaochangzuoyequExecute extends AbstractJobExecuteExecute {

    @Autowired
    private LiaochangzuoyequWriter liaochangzuoyequWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return liaochangzuoyequWriter;
    }
}
