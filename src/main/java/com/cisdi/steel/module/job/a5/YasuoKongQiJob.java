package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.YasuoKongQiExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 压缩空气生产情况汇总表
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class YasuoKongQiJob extends AbstractBaseCommonExportJob5 {

    @Autowired
    private YasuoKongQiExecute yasuoKongQiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_yasuokongqi;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return yasuoKongQiExecute;
    }
}
