package com.cisdi.steel.module.job;

import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

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
     * 获取请求数据
     *
     * @param template  模板
     * @param dateQuery 时间段
     * @return 结果
     */
    List<Map<String, Object>> requestApiData(ReportCategoryTemplate template, DateQuery dateQuery);

    /**
     * 写入数据
     *
     * @param excelDTO 写入的相关信息
     * @return 写入后的数据
     */
    Workbook writerExcel(WriterExcelDTO excelDTO);
}
