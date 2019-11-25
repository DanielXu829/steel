package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.ShaoJieKuangLiHuaExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8#烧结矿理化指标报表 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class ShaoJieKuangLiHua8Job extends AbstractExportJob {

    @Autowired
    ShaoJieKuangLiHuaExecute shaoJieKuangLiHuaExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_shaojiekuanglihua8;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return shaoJieKuangLiHuaExecute;
    }
}
