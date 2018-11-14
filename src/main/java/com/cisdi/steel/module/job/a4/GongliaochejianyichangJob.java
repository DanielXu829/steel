package com.cisdi.steel.module.job.a4;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a4.execute.GongliaochejianyichangJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GongliaochejianyichangJob extends AbstractExportJob {

    @Autowired
    private GongliaochejianyichangJobExecute gongliaochejianyichangJobExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.ygl_yichanggenzong;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return gongliaochejianyichangJobExecute;
    }
}
