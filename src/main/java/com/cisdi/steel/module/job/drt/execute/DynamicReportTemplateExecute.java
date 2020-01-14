package com.cisdi.steel.module.job.drt.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.drt.writer.DynamicReportTemplateWriter;
import com.cisdi.steel.module.job.gl.writer.KaoHeYueBaoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 动态报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2020/1/6 </P>
 *
 * @version 1.0
 */
@Component
public class DynamicReportTemplateExecute extends AbstractJobExecuteExecute {

    @Autowired
    private DynamicReportTemplateWriter dynamicReportTemplateWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return dynamicReportTemplateWriter;
    }
}
