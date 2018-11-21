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
 * 炼焦报表
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class LianjiaoWriter extends AbstractExcelReadWriter {
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
                if ("_jhlwjic6_day_hour".equals(sheetName)) {
                    List<CellData> cellData = mapDataHandler2(1, getUrl2(), columns, date, "CO6");
                    ExcelWriterUtil.setCellValue(sheet, cellData);
                } else if ("_jhlwjic7_day_hour".equals(sheetName)) {
                    List<CellData> cellData = mapDataHandler2(1, getUrl2(), columns, date, "CO7");
                    ExcelWriterUtil.setCellValue(sheet, cellData);
                } else {
                    for (int j = 0; j < size; j++) {
                        DateQuery item = dateQueries.get(j);
                        int rowIndex = j + 1;
                        List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(), columns, item);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    }
                }

            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        int size = columns.size();
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                queryParam.put("tagName", column);
                String result = httpUtil.get(url, queryParam);
                if (StringUtils.isNotBlank(result)) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (Objects.nonNull(jsonObject)) {
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        if (Objects.nonNull(rows)) {
                            JSONObject obj = rows.getJSONObject(0);
                            if (Objects.nonNull(obj)) {
                                Double val = obj.getDouble("val");
                                ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler2(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = getQueryParam2(dateQuery, version);
        List<CellData> cellDataList = new ArrayList<>();
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (Objects.nonNull(rows)) {
                    for (int j = 0; j < rows.size(); j++) {
                        JSONObject obj = rows.getJSONObject(j);
                        if (Objects.nonNull(obj)) {
                            List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, j + 1, obj);
                            cellDataList.addAll(cellData);
                        }
                    }

                }
            }

        }
        return cellDataList;
    }

    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String dateTime = DateUtil.getFormatDateTime(calendar.getTime(), DateUtil.fullFormat);
        result.put("time", dateTime);
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery, String version) {
        Map<String, String> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateQuery.getRecordDate());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
//        result.put("date", calendar.getTime().getTime() + "");
        result.put("date", "1540137600000");
        result.put("jlno", version);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        return result;
    }

    protected String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

    protected String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/tmmirbtmpDataTable/selectByDateAndType";
    }
}
