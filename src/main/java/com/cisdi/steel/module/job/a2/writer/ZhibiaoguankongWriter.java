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
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 指标管控
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ZhibiaoguankongWriter extends AbstractExcelReadWriter {
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
                DateQuery dateQuery = null;
                String shift = "";
                String shiftDate = "";
                int shiftNum = 0;
                if (DateUtil.isEffectiveDate(date.getRecordDate(), dateQueries.get(0).getStartTime(), dateQueries.get(0).getEndTime())) {
                    dateQuery = dateQueries.get(0);
                    shift = "夜班";
                    shiftDate = "00:00";
                    shiftNum = 1;
                } else if (DateUtil.isEffectiveDate(date.getRecordDate(), dateQueries.get(1).getStartTime(), dateQueries.get(1).getEndTime())) {
                    dateQuery = dateQueries.get(1);
                    shift = "白班";
                    shiftDate = "08:00";
                    shiftNum = 2;
                } else {
                    dateQuery = dateQueries.get(2);
                    shift = "中班";
                    shiftDate = "16:00";
                    shiftNum = 3;
                }
                int rowIndex = 1;
                if ("crushing".equals(sheetSplit[1])) {
                    Double cellDataList = this.valHandler(getUrl(), dateQuery);
                    setSheetValue(sheet, 1, 0, cellDataList);
                } else if ("lianjiao".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = this.mapDataHandler(getUrl2(), columns, 1, dateQuery, shiftNum);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("tag".equals(sheetSplit[1])) {
                    List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl3(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else {
                    Row row = sheet.createRow(1);
                    row.createCell(2).setCellValue(shift);
                    row.getCell(2).setCellType(CellType.STRING);
                    row.createCell(3).setCellValue(shiftDate);
                    row.getCell(3).setCellType(CellType.STRING);
                    List<CellData> cellDataList = mapDataHandler1(rowIndex, getUrl4(), columns, dateQuery);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam2(dateQuery);
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
                            Double val = data.getDouble(column);
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val);
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler1(Integer rowIndex, String url, List<String> columns, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam1(dateQuery);
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
                            for (int j = 0; j < rows.size(); j++) {
                                JSONObject obj = rows.getJSONObject(j);
                                if (Objects.nonNull(obj)) {
                                    String clock = obj.getString("clock");
                                    Date date = DateUtil.strToDate(clock, DateUtil.fullFormat);
//                                    Date endTime = dateQuery.getStartTime();
//                                    Date startTime = DateUtil.addHours(endTime, -8);
//                                    if (DateUtil.isEffectiveDate(date, startTime, endTime)) {
                                    Map<String, String> queryParam1 = getQueryParam3(DateUtil.getFormatDateTime(date, "yyyy/MM/dd HH:mm:00"));
                                    queryParam1.put("tagName", "CK67_L1R_CB_CBAcTol_1m_avg");
                                    String result1 = httpUtil.get(getUrl5(), queryParam1);
                                    if (StringUtils.isNotBlank(result1)) {
                                        JSONObject jsonObject1 = JSONObject.parseObject(result1);
                                        JSONArray rows1 = jsonObject1.getJSONArray("rows");
                                        JSONObject obj1 = rows1.getJSONObject(0);
                                        if (Objects.nonNull(obj1)) {
                                            Double val1 = obj1.getDouble("val");
                                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, val1);
                                        }
                                    }
                                }
                            }
//                            }
                        }
                    }
                }
            }
        }
        return cellDataList;
    }

    private void setSheetValue(Sheet sheet, Integer rowNum, Integer columnNum, Object obj) {
        Row row = sheet.getRow(rowNum);
        if (Objects.isNull(row)) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(columnNum);
        if (Objects.isNull(cell)) {
            cell = row.createCell(columnNum);
        }
        PoiCustomUtil.setCellValue(cell, obj);
    }

    public Double valHandler(String url, DateQuery dateQuery) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return null;
        }
        JSONObject r = data.getJSONObject("particleDistribution");
        if (Objects.isNull(r)) {
            return null;
        }
        Double crushingFineness = r.getDouble("crushingFineness");
        return crushingFineness;
    }

    public List<CellData> mapDataHandler(String url, List<String> columns, int rowBatch, DateQuery dateQuery, int shiftNum) {
        Map<String, String> queryParam = getQueryParam1(dateQuery, shiftNum);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray array = JSONObject.parseArray(result);
        if (Objects.isNull(array)) {
            return null;
        }
        JSONObject jsonObject = array.getJSONObject(0);
        HashMap<String, Object> Map = new HashMap<>();
        Map.put("k2", jsonObject.getDouble("k2"));
        Map.put("coalLoadingCoefficient", jsonObject.getDouble("coalLoadingCoefficient"));
        Map.put("kAn", (jsonObject.getDouble("no1FurnaceKAn") + jsonObject.getDouble("no2FurnaceKAn")) / 2);
        Map.put("kAvg", (jsonObject.getDouble("no1FurnaceKAvg") + jsonObject.getDouble("no2FurnaceKAvg")) / 2);
        List<CellData> cellData = ExcelWriterUtil.handlerRowData(columns, rowBatch, Map);
        return cellData;
    }


    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("dateTime", DateUtil.getFormatDateTime(dateQuery.getRecordDate(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery, Integer shiftNum) {
        Map<String, String> result = new HashMap<>();
        result.put("date", DateUtil.getFormatDateTime(DateUtil.getDateBeginTime(dateQuery.getRecordDate()), "yyyy/MM/dd HH:mm:ss"));
        result.put("shift", shiftNum.toString());
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));

        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("type", "avg");
        return result;
    }

    protected Map<String, String> getQueryParam3(String date) {
        Map<String, String> result = new HashMap<>();
        result.put("time", date);
        return result;
    }

    private String getUrl() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getParticleDistributionLatest";
    }

    private String getUrl2() {
        return httpProperties.getUrlApiJHOne() + "/cokeActualPerformance/getCokeActuPerfByDateAndShift";
    }

    private String getUrl3() {
        return httpProperties.getUrlApiJHOne() + "/jhTagValue/getTagValueStatisticType";
    }

    private String getUrl4() {
        return httpProperties.getUrlApiJHOne() + "/manufacturingState/getTagValue";
    }

    private String getUrl5() {
        return httpProperties.getUrlApiJHOne() + "/coalBlendingStatus/getVauleByNameAndTime";
    }

}
