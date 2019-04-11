package com.cisdi.steel.module.job.a3;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a3.execute.TuoliuExecute;
import com.cisdi.steel.module.job.a3.execute.TuoliuTuoxiaoYearExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 5#烧结机脱硫脱硝生产运行报表-v1 年报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoliuTuoxiaoYearJob extends AbstractExportJob {

    @Autowired
    private TuoliuTuoxiaoYearExecute tuoliuTuoxiaoYearExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.sj_tuoliutuoxiao_year;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return tuoliuTuoxiaoYearExecute;
    }
}
