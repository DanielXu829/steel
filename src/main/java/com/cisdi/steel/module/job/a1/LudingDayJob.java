package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.a1.execute.LudingDayExecute;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 高炉冷却壁温度日报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LudingDayJob extends AbstractExportJob {

    @Autowired
    private LudingDayExecute ludingDayExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_ludingzhuangliaozuoye_day1;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return ludingDayExecute;
    }

}
