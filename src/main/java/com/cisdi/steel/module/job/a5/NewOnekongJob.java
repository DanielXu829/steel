package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 新一空压站运行记录表
 */
@Component
public class NewOnekongJob extends AbstractBaseCommonExportJob5 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_xinyikong;
    }
}
