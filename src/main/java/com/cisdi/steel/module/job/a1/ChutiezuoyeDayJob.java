package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.ChutiezuoyeDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 出铁作业日报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/8 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezuoyeDayJob extends AbstractExportJob {

    @Autowired
    private ChutiezuoyeDayExecute chutiezuoyeDayExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.chutiezuoye_day;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return chutiezuoyeDayExecute;
    }

    @Override
    public DateQuery getCurrentDateQuery() {
        return DateQueryUtil.buildToday();
    }
}
