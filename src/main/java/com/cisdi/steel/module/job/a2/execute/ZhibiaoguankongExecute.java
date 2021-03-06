package com.cisdi.steel.module.job.a2.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a2.writer.ZhibiaoguankongWriter;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
@Slf4j
public class ZhibiaoguankongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ZhibiaoguankongWriter zhibiaoguankongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhibiaoguankongWriter;
    }

    @Override
    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {

    }
}
