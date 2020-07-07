package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.NoneWriter;
import com.cisdi.steel.module.job.gl.writer.YueBaoHuiZongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉休风统计执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/07/07 </P>
 *
 * @version 1.0
 */
@Component
public class XiuFengTongJiExecute extends AbstractJobExecuteExecute {

    @Autowired
    private NoneWriter noneWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return noneWriter;
    }
}