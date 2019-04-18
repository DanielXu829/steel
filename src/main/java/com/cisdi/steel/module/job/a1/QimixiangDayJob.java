package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 上料日报
 */
@Component
public class QimixiangDayJob extends AbstractBaseCommonExportJob1 {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_qimixiang_day;
    }
}
