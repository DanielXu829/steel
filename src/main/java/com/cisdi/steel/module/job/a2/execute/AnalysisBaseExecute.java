package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.AnalysisBaseWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 检化验基础
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AnalysisBaseExecute extends AbstractJobExecuteExecute {

    @Autowired
    private AnalysisBaseWriter analysisBaseWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return analysisBaseWriter;
    }
}
