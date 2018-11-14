package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.TuoliuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 5# 6#脱硫系统运行日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoliuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private TuoliuWriter tuoliuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return tuoliuWriter;
    }
}
