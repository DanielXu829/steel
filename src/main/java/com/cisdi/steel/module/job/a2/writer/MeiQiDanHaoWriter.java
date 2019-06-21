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
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 焦化公共执行处理类
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class MeiQiDanHaoWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        String version = "67.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
        }
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                DateQuery item = dateQueries.get(0);
                item.setEndTime(DateUtil.addMinute(item.getEndTime(), -50));
                DateQuery dateQuery = new DateQuery(new Date());
                Date date1 = DateUtil.addDays(item.getStartTime(), -7);
                dateQuery.setRecordDate(date1);
                dateQuery.setStartTime(date1);
                dateQuery.setEndTime(item.getStartTime());
                dateQueries.add(dateQuery);
                for (int j = 1; j <= 12; j++) {
                    DateQuery dateQuery1 = new DateQuery(new Date());
                    Date date2 = DateUtil.addMonths(item.getRecordDate(), -j);
                    dateQuery1.setRecordDate(DateQueryUtil.getMonthStartTime(date2));
                    dateQuery1.setStartTime(DateQueryUtil.getMonthStartTime(date2));
                    dateQuery1.setEndTime(DateQueryUtil.getMonthEndTime(date2));
                    dateQueries.add(dateQuery1);
                }
                for (int j = 0; j < dateQueries.size(); j++) {
                    int rowIndex = 15 - j;
                    DateQuery dateQuery1 = dateQueries.get(j);
                    List<CellData> cellDataList = mapDataHandler(rowIndex, getUrl(version), columns, dateQuery1, version);
                    List<CellData> cellDataList1 = mapDataHandler1(rowIndex, getUrl2(version), dateQuery1);
                    List<CellData> cellDataList2 = mapDataHandler2(rowIndex, getUrl3(version), dateQuery1, version);
                    List<CellData> cellDataList3 = mapDataHandler2(rowIndex, getUrl4(version), dateQuery1, version);
                    cellDataList.addAll(cellDataList1);
                    cellDataList.addAll(cellDataList2);
                    cellDataList.addAll(cellDataList3);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(int rowIndex, String url, List<String> columns, DateQuery dateQuery, String version) {
        Map<String, String> queryParam = getQueryParam(dateQuery);
        List<CellData> cellDataList = new ArrayList<>();
        String column = columns.get(0);
        if (StringUtils.isNotBlank(column)) {
            queryParam.put("tagNames", column);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONArray arr = data.getJSONArray(column);
                        if (Objects.nonNull(arr) && arr.size() != 0) {
                            BigDecimal danhao = BigDecimal.ZERO;
                            Integer size = 0;
                            for (int i = 0; i < arr.size(); i++) {
                                JSONObject jsonObject1 = arr.getJSONObject(i);
                                String clock = jsonObject1.getString("clock");
                                String hh = DateUtil.getFormatDateTime(DateUtil.strToDate(clock, DateUtil.fullFormat), "HH");
                                if (Integer.valueOf(hh) == 0) {
                                    size++;
                                    Double val = jsonObject1.getDouble("val");
                                    Map<String, String> queryMap = new HashMap<>();
                                    Date date = DateUtil.strToDate(clock, DateUtil.fullFormat);
                                    queryMap.put("date", DateUtil.getFormatDateTime(date, "yyyy/MM/dd 00:00:00"));
                                    String result1 = httpUtil.get(getUrl1(version), queryMap);
                                    if (StringUtils.isNotBlank(result1)) {
                                        JSONObject obj = JSONObject.parseObject(result1);
                                        if (Objects.nonNull(obj)) {
                                            JSONObject data1 = obj.getJSONObject("data");
                                            if (Objects.nonNull(data1)) {
                                                Double currentYield = data1.getDouble("currentYield");
                                                if("67.0".equals(version)){
                                                    danhao = danhao.add(new BigDecimal(val).multiply(new BigDecimal(10000)).divide(new BigDecimal(currentYield), 6, BigDecimal.ROUND_HALF_UP));
                                                }else {
                                                    danhao = danhao.add(new BigDecimal(val).multiply(new BigDecimal(170)).divide(new BigDecimal(currentYield), 6, BigDecimal.ROUND_HALF_UP));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(size==0){
                                size=1;
                            }
                            ExcelWriterUtil.addCellData(cellDataList, rowIndex, 4, danhao.divide(new BigDecimal(size), 6, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }

        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler1(int rowIndex, String url, DateQuery dateQuery) {
        List<CellData> cellDataList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String brandcode = "";
            String anaitemname = "";
            if (i == 0) {
                brandcode = "CNCOX";
                anaitemname = "Mt";
            } else if (i == 1) {
                brandcode = "63207";
                anaitemname = "Q";
            } else {
                brandcode = "63201";
                anaitemname = "Q";
            }
            Map<String, String> queryParam = getQueryParam2(dateQuery, brandcode, anaitemname);
            String result = httpUtil.get(url, queryParam);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (Objects.nonNull(jsonObject)) {
                    Double data = jsonObject.getDouble("data");
                    if (i == 0) {
                        data = data / 100;
                    }
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, 5 + i, data);
                }
            }
        }
        return cellDataList;
    }

    protected List<CellData> mapDataHandler2(int rowIndex, String url, DateQuery dateQuery, String version) {
        List<CellData> cellDataList = new ArrayList<>();
        Map<String, String> queryParam = getQueryParam1(dateQuery);
        String result = httpUtil.get(url, queryParam);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.nonNull(jsonObject)) {
                Double data = jsonObject.getDouble("data");
                if (url.equals(getUrl4(version))) {
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, 9, data);
                } else {
                    ExcelWriterUtil.addCellData(cellDataList, rowIndex, 8, data);
                }
            }
        }
        return cellDataList;
    }


    @Override
    protected Map<String, String> getQueryParam(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("startDate", DateUtil.getFormatDateTime(dateQuery.getStartTime(), "yyyy/MM/dd HH:mm:ss"));
        result.put("endDate", DateUtil.getFormatDateTime(dateQuery.getEndTime(), "yyyy/MM/dd HH:mm:ss"));
        return result;
    }

    protected Map<String, String> getQueryParam1(DateQuery dateQuery) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(dateQuery.getStartTime(), DateUtil.fullFormat));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getEndTime(), DateUtil.fullFormat));
        return result;
    }

    protected Map<String, String> getQueryParam2(DateQuery dateQuery, String brandcode, String anaitemname) {
        Map<String, String> result = new HashMap<>();
        result.put("start", DateUtil.getFormatDateTime(dateQuery.getStartTime(), DateUtil.fullFormat));
        result.put("end", DateUtil.getFormatDateTime(dateQuery.getEndTime(), DateUtil.fullFormat));
        result.put("brandcode", brandcode);
        result.put("anaitemname", anaitemname);
        return result;
    }

    protected String getUrl(String version) {
        return httpProperties.getJHUrlVersion(version) + "/jhTagValue/getTagValue";
    }

    protected String getUrl1(String version) {
        return httpProperties.getJHUrlVersion(version) + "/cokingYieldAndNumberHoles/getYieldByDate";
    }

    protected String getUrl2(String version) {
        return httpProperties.getJHUrlVersion(version) + "/getAnalysisAvg";
    }

    protected String getUrl3(String version) {
        return httpProperties.getJHUrlVersion(version) + "/getCoalDeviation";
    }

    protected String getUrl4(String version) {
        return httpProperties.getJHUrlVersion(version) + "/getCokeHolesDeviation";
    }
}
