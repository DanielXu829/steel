package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.BianLiaoJiLuWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 变料执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/14 </P>
 *
 * @version 1.0
 */
@Component
public class BianLiaoJiLuExecute extends AbstractJobExecuteExecute {

    @Autowired
    private BianLiaoJiLuWriter bianLiaoJiLuWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return bianLiaoJiLuWriter;
    }
}
