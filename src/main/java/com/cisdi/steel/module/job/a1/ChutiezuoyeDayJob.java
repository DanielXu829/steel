package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
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
public class ChutiezuoyeDayJob extends AbstractBaseCommonExportJob1 {


    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chutiezuoye_day;
    }


}
