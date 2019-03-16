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
 * 焦炉加热
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JiaolujiareWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                Date date1 = DateUtil.addDays(dateQueries.get(0).getRecordDate(), -1);
                String formatDateTime = DateUtil.getFormatDateTime(date1, DateUtil.yyyyMMddFormat);
                Date date2 = DateUtil.strToDate(formatDateTime + " 23:30:00", DateUtil.fullFormat);
                DateQuery dateQuery = new DateQuery(dateQueries.get(0).getRecordDate());
                dateQuery.setRecordDate(date2);
                dateQuery.setStartTime(date2);
                dateQuery.setEndTime(DateUtil.getTodayBeginTime());
                dateQueries.add(0,dateQuery);
                int size = dateQueries.size();
                for (int j = 0; j < size; j++) {
                    DateQuery item = dateQueries.get(j);
                    if (item.getRecordDate().before(new Date())) {
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = this.mapDataHandler(rowIndex, getUrl(), columns, item,version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    } else {
                        break;
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery,String version) {
        Map<String, String> queryParam = getQueryParam(dateQuery,version);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagNames", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (Objects.nonNull(jsonObject)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (Objects.nonNull(data)) {
                            JSONArray arr = data.getJSONArray(column);
                            if (Objects.nonNull(arr)&& arr.size()!=0) {
                                JSONObject jsonObject1 = arr.getJSONObject(arr.size() - 1);
                                Double val = jsonObject1.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected Map<String, String> getQueryParam(DateQuery dateQuery,String version) {
        Map<String, String> result = new HashMap<>();
        if("6.0".equals(version)){
            Date date=DateUtil.addMinute(dateQuery.getRecordDate(),15);
            result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(date,-10),"yyyy/MM/dd HH:mm:ss"));
            result.put("endDate", DateUtil.getFormatDateTime(date,"yyyy/MM/dd HH:mm:ss"));
        }else if("7.0".equals(version)){
            Date date=DateUtil.addMinute(dateQuery.getRecordDate(),-5);
            result.put("startDate", DateUtil.getFormatDateTime(DateUtil.addMinute(date,-10),"yyyy/MM/dd HH:mm:ss"));
            result.put("endDate", DateUtil.getFormatDateTime(date,"yyyy/MM/dd HH:mm:ss"));
        }
       return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValue";
    }

}
