package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.JiejiMonthWriter;
import com.cisdi.steel.module.job.a3.writer.JiejiWriter;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 5#、6#烧结机生产月报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiejiMonthExecute extends AbstractJobExecuteExecute {

    @Autowired
    private JiejiMonthWriter jiejiMonthWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return jiejiMonthWriter;
    }
}
