package com.cisdi.steel.module.job.a6;

import com.cisdi.steel.module.job.a1.AbstractBaseCommonExportJob1;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 高炉本体温度日报表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BF6trtJob extends AbstractBaseCommonExportJob1 {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.hb_6bftrt;
    }
}
