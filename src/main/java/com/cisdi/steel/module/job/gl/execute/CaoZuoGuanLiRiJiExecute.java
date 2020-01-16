package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.CaoZuoGuanLiRiJiWriter;
import com.cisdi.steel.module.job.gl.writer.JiShuJingJiWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉操作管理日记 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class CaoZuoGuanLiRiJiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private CaoZuoGuanLiRiJiWriter caoZuoGuanLiRiJiWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return caoZuoGuanLiRiJiWriter;
    }
}
