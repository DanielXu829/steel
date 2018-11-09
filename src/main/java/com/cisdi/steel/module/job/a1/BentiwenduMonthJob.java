package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.BentiwenduDayExecute;
import com.cisdi.steel.module.job.a1.execute.BentiwenduMonthExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉本体温度日报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BentiwenduMonthJob extends AbstractExportJob {

    @Autowired
    private BentiwenduMonthExecute bentiwenduMonthExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_bentiwendu_month;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return bentiwenduMonthExecute;
    }
}
