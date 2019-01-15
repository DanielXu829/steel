package com.cisdi.steel.module.job.a1.execute;

import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a1.writer.ZhongdianbuweicanshuTubiaoWriter;
import com.cisdi.steel.module.job.a1.writer.ZhongdianbuweicanshuWriter;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2019/1/14 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhongdianbuweicanshuTubiaoExecute extends AbstractJobExecuteExecute {
    @Autowired
    private ZhongdianbuweicanshuTubiaoWriter zhongdianbuweicanshuTubiaoWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return zhongdianbuweicanshuTubiaoWriter;
    }

    @Override
    protected void replaceTemplatePath(ReportIndex reportIndex, ReportCategoryTemplate template) {
        // 什么都不做
    }
}
