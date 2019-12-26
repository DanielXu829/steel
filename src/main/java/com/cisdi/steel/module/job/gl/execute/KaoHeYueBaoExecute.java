package com.cisdi.steel.module.job.gl.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.gl.writer.KaoHeYueBaoWriter;
import com.cisdi.steel.module.job.gl.writer.LuLiaoXiaoHaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 8高炉考核月报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2019/11/22 </P>
 *
 * @version 1.0
 */
@Component
public class KaoHeYueBaoExecute extends AbstractJobExecuteExecute {

    @Autowired
    private KaoHeYueBaoWriter kaoHeYueBaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return kaoHeYueBaoWriter;
    }
}
