package com.cisdi.steel.module.job.a5;

import com.cisdi.steel.module.job.AbstractExportJob;
import com.cisdi.steel.module.job.IJobExecute;
import com.cisdi.steel.module.job.a5.execute.TwokongDayExecute;
import com.cisdi.steel.module.job.enums.JobEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
public class TwokongJob extends AbstractExportJob {

    @Autowired
    private TwokongDayExecute execute;

    @Override
    public JobEnum getCurrentJob() {
        return JobEnum.nj_twokong;
    }

    @Override
    public IJobExecute getCurrentJobExecute() {
        return execute;
    }
}
