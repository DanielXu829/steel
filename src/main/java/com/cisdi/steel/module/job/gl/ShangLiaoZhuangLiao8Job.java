package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.ChuTieHuaXueChengFenExecute;
import com.cisdi.steel.module.job.gl.execute.ShangLiaoZhuangLiaoExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#上料装料报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class ShangLiaoZhuangLiao8Job extends AbstractExportJob {

    @Autowired
    private ShangLiaoZhuangLiaoExecute shangLiaoZhuangLiaoExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_shangliaozhuangliao8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shangLiaoZhuangLiaoExecute;
    }
}
