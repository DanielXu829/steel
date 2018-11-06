package com.cisdi.steel.module.job.a1.readWriter;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.resp.ResponseUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LudingReadWriter extends AbstractExcelReadWriter {

    @Override
    public List<Map<String, Object>> requestApiData(ReportCategoryTemplate template, DateQuery dateQuery) {
        String url = httpProperties.getUrlApiGLOne() + "/batchenos/period";
        String s = httpUtil.get(url, dateQuery.getQueryParam());
        List<String> list = ResponseUtil.getResponseArray(s, String.class);

        if (Objects.isNull(list) || list.isEmpty()) {
            throw new NullPointerException("没有数据");
        }

        Collections.sort(list);
        List<Map<String, Object>> result = new ArrayList<>();
        for (String batchNo : list) {
            String detail = httpProperties.getUrlApiGLOne() + "/batch/" + batchNo;
            String detailData = httpUtil.get(detail);
            if (StringUtils.isNotBlank(detailData)) {
                Map<String, Object> mapType = JSON.parseObject(detailData, Map.class);
                result.add(mapType);
            }
        }
        return result;
    }

    @Override
    public Workbook writerExcel(WriterExcelDTO excelDTO) {
        List<Map<String, Object>> dataList = requestApiData(excelDTO.getTemplate(), excelDTO.getDateQuery());
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        // 第一个sheet值
        Sheet sheet = workbook.getSheet("_charge_day_each");
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        List<CellData> cellDataList = ExcelWriterUtil.loopRowData(dataList, columns, 6);
        Collections.sort(cellDataList);
        ExcelWriterUtil.setCellValue(sheet, cellDataList);

        return workbook;
    }
}
