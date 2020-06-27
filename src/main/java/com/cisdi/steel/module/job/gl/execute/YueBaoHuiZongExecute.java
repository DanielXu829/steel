package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.YueBaoHuiZongWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉冷月报汇总 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/06/24 </P>
 *
 * @version 1.0
 */
@Component
public class YueBaoHuiZongExecute  extends AbstractJobExecuteExecute {

    @Autowired
    private YueBaoHuiZongWriter yueBaoHuiZongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return yueBaoHuiZongWriter;
    }
}
