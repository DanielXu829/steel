package com.cisdi.steel.module.job.a5.writer;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.enums.JobEnum;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 电力
 */
@Component
public class DianLiWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                List<CellData> cellDataList = new ArrayList<>();
                eachData(columns, excelDTO.getJobEnum().getCode(), cellDataList, date);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
        }
        return workbook;
    }


    private void eachData(List<String> columns, String code, List<CellData> cellDataList, DateQuery date) {
        //界牌岭
        List<String> dateList = new ArrayList<>();
        if (code.equals(JobEnum.nj_jiepailing_day.getCode())) {
            DateQueryUtil.buildJiePaiLing(dateList, date.getRecordDate(), 0);
            DateQueryUtil.buildJiePaiLing(dateList, date.getRecordDate(), 7);
            DateQueryUtil.buildJiePaiLing(dateList, date.getRecordDate(), 15);
            DateQueryUtil.buildJiePaiLing(dateList, date.getRecordDate(), 23);
            //功率因素统计
        } else if (code.equals(JobEnum.nj_gongluyinsutongji_month.getCode())) {
            DateQueryUtil.buildJiePaiLing(dateList, DateQueryUtil.getMonthSomeTime(date.getRecordDate(), 1), 0);
            DateQueryUtil.buildJiePaiLing(dateList, DateQueryUtil.getMonthSomeTime(date.getRecordDate(), 8), 0);
            DateQueryUtil.buildJiePaiLing(dateList, DateQueryUtil.getMonthSomeTime(date.getRecordDate(), 15), 0);
            DateQueryUtil.buildJiePaiLing(dateList, DateQueryUtil.getMonthSomeTime(date.getRecordDate(), 22), 0);
        } else if (code.equals(JobEnum.nj_jidu_year.getCode())) {
            List<DateQuery> dateQueries = DateQueryUtil.buildYearMonthEach(date.getRecordDate());
            dateQueries.forEach(dateQuery -> {
                DateQueryUtil.buildJiePaiLing(dateList, dateQuery.getStartTime(), 0);
            });
        }

        String result = getData(columns, dateList);
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            if (Objects.nonNull(obj)) {
                int row = 1;
                for (int i = 0; i < dateList.size(); i++) {
                    String s = dateList.get(i);
                    JSONObject jsonObject = obj.getJSONObject(s);
                    if (Objects.nonNull(jsonObject)) {
                        for (int j = 0; j < columns.size(); j++) {
                            String c = columns.get(j);
                            if (StringUtils.isNotBlank(c)) {
                                Object o = jsonObject.get(c);
                                ExcelWriterUtil.addCellData(cellDataList, row, j, o);
                            }
                        }
                    }
                    row++;
                }
            }
        }
    }

    private String getData(List<String> col, List<String> dateList) {
        JSONObject jsonObject = new JSONObject();
        dateList.forEach(date -> {
            jsonObject.put(date, col);
        });
        String re1 = httpUtil.postJsonParams(httpProperties.getUrlApiNJOne() + "/ElecTagValues", jsonObject.toJSONString());
        return re1;
    }
}
