package com.cisdi.steel.module.job.a5.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a5.writer.MeiqihunhemeiWriter;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 煤气柜作业区混合煤气情况表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeiqihunhemeiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private MeiqihunhemeiWriter meiqihunhemeiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return meiqihunhemeiWriter;
    }

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        super.execute(jobExecuteInfo);
    }
}
