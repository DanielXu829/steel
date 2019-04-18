package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 装料除尘日报
 */
@Component
public class ZhuangliaochuchenDayJob extends AbstractBaseCommonExportJob1 {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_zhuangliaochuchen;
    }
}
