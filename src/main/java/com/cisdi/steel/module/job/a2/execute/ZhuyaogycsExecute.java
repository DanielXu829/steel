package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.ZhuyaogycsWriter;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ZhuyaogycsExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ZhuyaogycsWriter zhuyaogycsWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhuyaogycsWriter;
    }

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        //生成今天的
        super.execute(jobExecuteInfo);
    }
}
