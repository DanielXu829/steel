package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 高炉日报表
 */
@Component
public class GaoLuDayJob extends AbstractBaseCommonExportJob1 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_jswgaolu_day;
    }

}
