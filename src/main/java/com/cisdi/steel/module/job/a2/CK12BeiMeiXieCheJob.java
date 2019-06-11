package com.cisdi.steel.module.job.a2;

import com.cisdi.steel.module.job.a5.task.AbstractNJExportJob;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 备煤卸车
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class CK12BeiMeiXieCheJob extends AbstractNJExportJob {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.jh_ck12beimeixieche;
    }

}
