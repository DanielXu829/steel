package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.GuankongzhibiaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 管控指标
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GuankongzhibiaoExecute extends AbstractJobExecuteExecute {


    @Autowired
    private GuankongzhibiaoWriter guankongzhibiaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return guankongzhibiaoWriter;
    }
}
