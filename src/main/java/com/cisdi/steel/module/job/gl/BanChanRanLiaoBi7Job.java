package com.cisdi.steel.module.job.gl;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.gl.execute.BanChanRanLiaoBiExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 7高炉班产燃料比 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/09 </P>
 *
 * @version 1.0
 */
@Component
public class BanChanRanLiaoBi7Job extends AbstractExportJob {

    @Autowired
    BanChanRanLiaoBiExecute banChanRanLiaoBiExecute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.gl_banchanranliaobi7;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return banChanRanLiaoBiExecute;
    }
}