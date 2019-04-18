package com.cisdi.steel.module.job.a1;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a1.execute.ZhongdianbuweicanshuTubiaoExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 重点部位参数监控图表报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhongdianbuweicanshuTubiaoJob extends AbstractExportJob {


    @Autowired
    private ZhongdianbuweicanshuTubiaoExecute zhongdianbuweicanshuTubiaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_zhongdianbuweicanshutubiao;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return zhongdianbuweicanshuTubiaoExecute;
    }

}
