package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#小时炉况输入表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/13</P>
 *
 * @version 1.0
 */
@Component
public class LuKuangXiaoShi8Job extends AbstractBaseExportJob {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lukuangxiaoshi8;
    }
}
