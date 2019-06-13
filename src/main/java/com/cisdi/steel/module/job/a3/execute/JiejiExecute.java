package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.JiejiWriter;
import com.cisdi.steel.module.job.dto.JobExecuteInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 5#、6#烧结机生产日报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiejiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private JiejiWriter jiejiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return jiejiWriter;
    }

    @Override
    public void execute(JobExecuteInfo jobExecuteInfo) {
        //生成今天的
        super.execute(jobExecuteInfo);
        //生成
        if (Objects.isNull(jobExecuteInfo.getIndexId())) {
            super.executeDateParam(jobExecuteInfo, -1);
        }
    }
}
