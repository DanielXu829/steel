package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 数据 获取  和写入
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface IExcelReadWriter {

    /**
     * 写入数据
     *
     * @param excelDTO 写入的相关信息
     * @return 写入后的数据
     */
    Workbook writerExcelExecute(WriterExcelDTO excelDTO);
}
