package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 三空压站启停次数表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ThreekongCountJob extends AbstractBaseCommonExportJob5 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_threekongcount;
    }
}
