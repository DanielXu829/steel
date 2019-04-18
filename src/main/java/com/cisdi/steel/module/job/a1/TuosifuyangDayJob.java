package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 脱湿富氧日报
 */
@Component
public class TuosifuyangDayJob extends AbstractBaseCommonExportJob1 {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_tuosifuyang;
    }
}
