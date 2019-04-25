package com.cisdi.steel.module.job.a2.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 除尘公共执行处理类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class BaseChuChenWriter extends BaseJhWriter {
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
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int size = dateQueries.size();
                if ("tag".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        if (item.getRecordDate().before(new Date())) {
                            int rowIndex = j + 1;
                            List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(), columns, item);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        } else {
                            break;
                        }
                    }
                } else if ("speed".equals(sheetSplit[1])) {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        double speedVal = tagVal(getUrl(), columns.get(1), item);
                        item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),-1));
                        double speedVal1 = tagVal(getUrl(), columns.get(1), item);
                        if((Math.abs(speedVal-speedVal1))>50){
                            item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),-1));
                            double speedVal2 = tagVal(getUrl(), columns.get(1), item);
                            item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),-1));
                            double speedVal3 = tagVal(getUrl(), columns.get(1), item);
                            if((Math.abs(speedVal2-speedVal3))>50){
                                item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),-1));
                            }else {
                                item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),1));
                            }
                        }else {
                            item.setRecordDate(DateUtil.addMinute(item.getRecordDate(),1));
                        }
                        if (item.getRecordDate().before(new Date())) {
                            int rowIndex = j + 1;
                            List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(), columns, item);
                            ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        } else {
                            break;
                        }
                    }
                    
                }
            }
        }
        return workbook;
    }

    protected double tagVal(String url,String tagName, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        queryParam.put("tagNames", tagName);
        String result = httpUtil.get(url, queryParam);
        Double val=0.0;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONObject data = jsonObject.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONArray arr = data.getJSONArray(tagName);
                    if (Objects.nonNull(arr) && arr.size() != 0) {
                        JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                        val = jsonObject1.getDouble("val");
                    }
                }
            }
        }
        return val;
    }

}
