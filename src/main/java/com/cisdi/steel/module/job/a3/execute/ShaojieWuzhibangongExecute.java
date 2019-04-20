package com.cisdi.steel.module.job.a3.execute;

import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.AbstractJobExecuteExecute;
import com.cisdi.steel.module.job.IExcelReadWriter;
import com.cisdi.steel.module.job.a3.writer.GycanshuWriter;
import com.cisdi.steel.module.job.a3.writer.ShaojieWuzhibangongWriter;
import com.cisdi.steel.module.job.dto.ExcelPathInfo;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 烧结无纸办公通用执行类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ShaojieWuzhibangongExecute extends AbstractJobExecuteExecute {

    @Autowired
    private ShaojieWuzhibangongWriter wuzhibangongWriter;

    @Override
    public IExcelReadWriter getCurrentExcelWriter() {
        return wuzhibangongWriter;
    }
}
