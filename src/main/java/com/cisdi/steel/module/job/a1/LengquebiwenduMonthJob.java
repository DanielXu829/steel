package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 高炉冷却壁温度月报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LengquebiwenduMonthJob extends AbstractBaseCommonExportJob1 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_lengquebiwendu_month;
    }

}
