package com.cisdi.steel.module.job.a4.writer;

import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

/**
 * 煤头外派
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeitouwaipaiMonthWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return getMapHandler(getUrl(), 1, excelDTO);
    }

    private String getUrl() {
        return httpProperties.getUrlApiYGLOne() + "/reportManager/getReport4Data";
    }
}
