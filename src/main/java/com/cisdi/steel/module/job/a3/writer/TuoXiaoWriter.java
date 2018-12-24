package com.cisdi.steel.module.job.a3.writer;

import cn.afterturn.easypoi.util.PoiCellUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 脱销运行记录月报
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TuoXiaoWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        return this.getMapHandler2(excelDTO, version);
    }

    /**
     * 同样处理 方式
     *
     * @param excelDTO 数据
     * @return 结果
     */
    public Workbook getMapHandler2(WriterExcelDTO excelDTO, String version) {
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

                if ("_6tuoxiaogaoliu_month_day".equals(sheetName)) {
                    int rowBaatch = 1;
                    List<String> towColumns = PoiCustomUtil.getRowCelVal(sheet, 1);
                    DateQuery dateQuery = DateQueryUtil.buildMonth(date.getRecordDate());
                    dateQuery.setEndTime(dateQuery.getRecordDate());
                    List<CellData> cellDataList = mapDataHandler(getUrl(version), columns, towColumns, dateQuery, rowBaatch, sheetName);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                } else if ("_6tuoxiaogaoliu1_month_day".equals(sheetName)) {
                    int rowBaatch = 1;
                    for (DateQuery dateQuery : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler(getUrl1(version), columns, null, dateQuery, rowBaatch, sheetName);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        rowBaatch += 6;
                    }
                }
            }
        }
        return workbook;
    }

    private List<CellData> mapDataHandler(String url, List<String> columns, List<String> towColumns, DateQuery dateQuery, int rowBatch, String sheetName) {
        JSONObject query = new JSONObject();
        query.put("start", dateQuery.getQueryStartTime());
        query.put("end", dateQuery.getQueryEndTime());
        query.put("tagNames", columns);

        if ("_6tuoxiaogaoliu1_month_day".equals(sheetName)) {
            query.put("method", "sum");
        }
        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(query, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);


        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject data = jsonObject.getJSONObject("data");

        if ("_6tuoxiaogaoliu_month_day".equals(sheetName)) {
            query.put("tagNames", towColumns);
            jsonString = JSONObject.toJSONString(query, serializeConfig);
            String result2 = httpUtil.postJsonParams(url, jsonString);
            JSONObject jsonObject2 = JSONObject.parseObject(result2);
            JSONObject data2 = jsonObject2.getJSONObject("data");
            if (Objects.isNull(data)) {
                return null;
            }
            return handlerJsonArray(columns, towColumns, data, data2, rowBatch, dateQuery);
        } else {
            return comm(url, columns, dateQuery, rowBatch);
        }

    }


    private List<CellData> comm(String url, List<String> columns, DateQuery dateQuerys, int rowBatch) {
        List<CellData> cellDataList = new ArrayList<>();
        List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuerys.getStartTime());
        int rowBatchs = rowBatch;
        for (DateQuery dateQuery : dateQueries8) {
            JSONObject query = new JSONObject();
            query.put("start", dateQuery.getQueryStartTime());
            query.put("end", dateQuery.getQueryEndTime());
            query.put("tagNames", columns);
            query.put("method", "sum");
            SerializeConfig serializeConfig = new SerializeConfig();
            String jsonString = JSONObject.toJSONString(query, serializeConfig);
            String result = httpUtil.postJsonParams(url, jsonString);

            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject data = jsonObject.getJSONObject("data");
            if (Objects.isNull(data)) {
                return null;
            }
            handlerJsonArray1(cellDataList, columns, data, rowBatchs);
            rowBatchs += 2;
        }
        return cellDataList;
    }


    //
    private List<CellData> handlerJsonArray1(List<CellData> cellDataList, List<String> columns, JSONObject data, int rowBatch) {

        int size = columns.size();
        int rowIndex = rowBatch;
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            if (StringUtils.isNotBlank(column)) {
                Object jsonObject = data.get(column);
                Object v = "";
                if (Objects.nonNull(jsonObject)) {
                    v = jsonObject;
                }
                ExcelWriterUtil.addCellData(cellDataList, rowIndex++, i, v);
            }
        }
        return cellDataList;
    }

    private List<CellData> handlerJsonArray(List<String> columns, List<String> towColumns, JSONObject data, JSONObject data2, int rowBatch, DateQuery dateQuerys) {
        List<CellData> cellDataList = new ArrayList<>();
        int size = columns.size();
        for (int i = 0; i < size; i++) {
            String column = columns.get(i);
            int rowIndex = rowBatch + 1;
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data.getJSONObject(column);
                if (Objects.nonNull(jsonObject)) {
                    Map<String, Object> innerMap = jsonObject.getInnerMap();
                    Set<String> keys = innerMap.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    List<DateQuery> all = new ArrayList<>();
                    List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(dateQuerys.getRecordDate());


                    for (DateQuery dateQuery : dateQueries) {
                        List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuery.getStartTime());
                        all.addAll(dateQueries8);
                    }

                    for (int j = 0; j < all.size(); j++) {
                        long time = all.get(j).getStartTime().getTime();
                        Object v = "";
                        for (int m = 0; m < list.length; m++) {
                            if (time == list[m].longValue()) {
                                Object o = innerMap.get(String.valueOf(list[m]));
                                v = o;
                                break;
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, v);
                        rowIndex += 2;
                    }
                }
            }
        }
        int size2 = towColumns.size();
        for (int i = 0; i < size2; i++) {
            String column = towColumns.get(i);
            int rowIndex = rowBatch + 2;
            if (StringUtils.isNotBlank(column)) {
                JSONObject jsonObject = data2.getJSONObject(column);
                if (Objects.nonNull(jsonObject)) {
                    Map<String, Object> innerMap = jsonObject.getInnerMap();
                    Set<String> keys = innerMap.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    Arrays.sort(list);

                    List<DateQuery> all = new ArrayList<>();
                    List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(dateQuerys.getRecordDate());


                    for (DateQuery dateQuery : dateQueries) {
                        List<DateQuery> dateQueries8 = DateQueryUtil.buildDay8HourEach(dateQuery.getStartTime());
                        all.addAll(dateQueries8);
                    }

                    for (int j = 0; j < all.size(); j++) {
                        long time = all.get(j).getStartTime().getTime();
                        Object v = "";
                        for (int m = 0; m < list.length; m++) {
                            if (time == list[m].longValue()) {
                                Object o = innerMap.get(String.valueOf(list[m]));
                                v = o;
                                break;
                            }
                        }
                        ExcelWriterUtil.addCellData(cellDataList, rowIndex, i, v);
                        rowIndex += 2;
                    }
                }
            }
        }

        return cellDataList;
    }

    /**
     * 不同的版本获取不同的接口地址
     *
     * @param version 版本号
     * @return 结果
     */
    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValues/tagNames";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValues/tagNames";
        }
    }

    /**
     * post
     *
     * @param version 版本
     * @return
     */
    private String getUrl1(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/tagValueAction";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/tagValueAction";
        }
    }
}
