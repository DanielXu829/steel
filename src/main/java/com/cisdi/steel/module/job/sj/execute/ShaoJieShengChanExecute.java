package com.cisdi.steel.module.job.sj.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.sj.writer.ShaoJieShengChanWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 烧结生产执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/25 </P>
 *
 * @version 1.0
 */
@Component
public class ShaoJieShengChanExecute extends AbstractJobExecuteExecute {

    @Autowired
    public ShaoJieShengChanWriter shaoJieShengChanWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return shaoJieShengChanWriter;
    }

}
