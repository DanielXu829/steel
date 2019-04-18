package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 轴流压缩机日报
 */
@Component
public class ZhouliuyasuojiDayJob extends AbstractBaseCommonExportJob1 {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_zhouliuyasuoji;
    }
}
