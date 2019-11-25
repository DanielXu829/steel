package com.cisdi.steel.module.job.gl;


import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.BianLiaoJiLuExecute;
import com.cisdi.steel.module.job.gl.execute.ChuTieHuaXueChengFenExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#出铁化学成分报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class ChuTieHuaXueChengFen8Job extends AbstractExportJob {

    @Autowired
    private ChuTieHuaXueChengFenExecute chuTieHuaXueChengFenExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_chutiehuaxuechengfen8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return chuTieHuaXueChengFenExecute;
    }
}
