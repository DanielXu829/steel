package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 热风炉日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/21 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class RefengluDayJob extends AbstractBaseCommonExportJob1 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_refenglu_day;
    }
}
