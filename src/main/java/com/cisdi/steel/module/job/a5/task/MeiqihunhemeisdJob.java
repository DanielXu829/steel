package com.cisdi.steel.module.job.a5.task;

import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.stereotype.Component;

/**
 * 煤气柜作业区混合煤气情况表-人工录入
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeiqihunhemeisdJob extends AbstractNJExportJob {

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_meiqihunhemeisd_month;
    }

}
