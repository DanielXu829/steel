package com.cisdi.steel.module.job.a5.task;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class QiguidianjianruihuaMonthJob  extends AbstractNJExportJob {
    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_qiguidianjianruihua_month;
    }
}
