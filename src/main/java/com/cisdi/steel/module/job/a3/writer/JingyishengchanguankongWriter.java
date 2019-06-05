package com.cisdi.steel.module.job.a3.writer;

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
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 精益生产管控系统
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/12 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class JingyishengchanguankongWriter extends AbstractExcelReadWriter {

    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        return this.getMapHandler1(excelDTO);
    }

    protected Workbook getMapHandler1(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 以下划线开头的sheet 表示 隐藏表  待处理
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                // 获取的对应的策略
                List<DateQuery> dateQueries = DateQueryUtil.buildMonthDayEach(date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                //接口地址
                int jk = 1;
                //开始行数
                int index = 1;
                //每小时还是每8小时
                int flag = 1;
                //每次跳行数
                int rowBatch = 24;
                if ("_tag_month_day".equals(sheetName)) {
                    jk = 1;
                    flag = 1;
                    rowBatch = 24;
                } else if ("_tag_month_shift".equals(sheetName)) {
                    jk = 1;
                    flag = 2;
                    rowBatch = 3;
                } else if ("_gyk_month_day".equals(sheetName)) {
                    jk = 2;
                    flag = 1;
                    rowBatch = 24;
                } else if ("_jhy_month_day".equals(sheetName)) {
                    jk = 3;
                    flag = 1;
                    rowBatch = 24;
                } else if ("_cfyc_month_day".equals(sheetName)) {
                    jk = 4;
                    rowBatch = 24;
                }
                for (DateQuery item : dateQueries) {
                    List<CellData> cellDataList = mapDataHandler(columns, item, index, version, flag, jk);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                    index += rowBatch;
                }
            }
        }
        return workbook;
    }

    /**
     * @param columns
     * @param dateQuery
     * @param index
     * @param version
     * @param t
     * @param jk
     * @return
     */
    protected List<CellData> mapDataHandler(List<String> columns, DateQuery dateQuery, int index, String version, int t, int jk) {
        String result = "";
        if (jk == 1) {
            result = getTagValues(getUrl(version), dateQuery, columns);
        } else if (jk == 2) {
            result = getTagValues(getUrl1(version), dateQuery, columns);
        } else if (jk == 3) {
            Map<String, String> map = new HashMap<>();
            map.put("brandCode", "ore_blending");
            map.put("timeType", "sampleTime");
            map.put("pageSize", Integer.MAX_VALUE + "");
            map.put("startTime", dateQuery.getQueryStartTime().toString());
            map.put("endTime", dateQuery.getQueryEndTime().toString());
            result = httpUtil.get(getUrl2(version), map);
            if (StringUtils.isBlank(result)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.isNull(jsonObject)) {
                return null;
            }
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return dealSub2(columns, data2, dateQuery, index);
        } else if (jk == 4) {
            Map<String, String> map = new HashMap<>();
            map.put("pageSize", Integer.MAX_VALUE + "");
            map.put("startTime", dateQuery.getQueryStartTime().toString());
            map.put("endTime", dateQuery.getQueryEndTime().toString());
            result = httpUtil.get(getUrl3(version), map);
            if (StringUtils.isBlank(result)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (Objects.isNull(jsonObject)) {
                return null;
            }
            JSONArray data2 = jsonObject.getJSONArray("data");
            if (Objects.isNull(data2) || data2.size() == 0) {
                return null;
            }
            return dealSub3(columns, data2, dateQuery, index);
        }

        return dealSub1(result, columns, dateQuery, index, t);
    }

    private List<CellData> dealSub1(String result, List<String> columns, DateQuery dateQuery, int index, int t) {
        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            JSONObject obj = JSONObject.parseObject(result);
            obj = obj.getJSONObject("data");
            if (Objects.nonNull(obj)) {
                for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                    int indexs = index;
                    String cell = columns.get(columnIndex);
                    if (StringUtils.isNotBlank(cell)) {
                        JSONObject data = obj.getJSONObject(cell);
                        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
                        if (t == 2) {
                            dayHourEach = DateQueryUtil.buildDay8HourEach(dateQuery.getRecordDate());
                        }
                        for (int i = 0; i < dayHourEach.size(); i++) {
                            Object v = "";
                            if (Objects.nonNull(data)) {
                                Map<String, Object> innerMap = data.getInnerMap();
                                Set<String> keySet = innerMap.keySet();
                                Long[] list = new Long[keySet.size()];
                                int k = 0;
                                for (String key : keySet) {
                                    list[k] = Long.valueOf(key);
                                    k++;
                                }
                                Arrays.sort(list);
                                Date startTime = dayHourEach.get(i).getEndTime();
                                for (int j = 0; j < list.length; j++) {
                                    Long tempTime = list[j];
                                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);

                                    String formatDateTime1 = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:mm:00");
                                    Date date1 = DateUtil.strToDate(formatDateTime1, DateUtil.fullFormat);
                                    Long betweenMin = DateUtil.getBetweenMin(date1, startTime);
                                    long abs = Math.abs(betweenMin);

                                    if (date.getTime() == startTime.getTime() || abs == 1) {
                                        v = data.get(tempTime + "");
                                        break;
                                    }
                                }
                            }
                            ExcelWriterUtil.addCellData(resultList, indexs++, columnIndex, v);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    protected List<CellData> dealSub2(List<String> columns, JSONArray data, DateQuery dateQuery, int startRow) {
        List<CellData> cellDataList = new ArrayList<>();

        List<JSONObject> list = new ArrayList<>();
        List<JSONObject> list2 = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject o = data.getJSONObject(i);
            if (Objects.nonNull(o)) {
                JSONObject analysis = o.getJSONObject("analysis");
                if ("LC".equals(analysis.get("type"))) {
                    list.add(o);
                }

                if ("LP".equals(analysis.get("type"))) {
                    list2.add(o);
                }
            }
        }

        for (int j = 0; j < list.size(); j++) {
            JSONObject o1 = list.get(j);
            if (Objects.nonNull(o1)) {
                JSONObject analysis = o1.getJSONObject("analysis");
                String sampleid = analysis.getString("sampleid");
                for (int m = 0; m < list2.size(); m++) {
                    JSONObject o2 = list2.get(m);
                    if (Objects.nonNull(o2)) {
                        JSONObject analysis2 = o2.getJSONObject("analysis");
                        String sampleid2 = analysis2.getString("sampleid");
                        if (sampleid.equals(sampleid2)) {
                            JSONObject values1 = o1.getJSONObject("values");
                            JSONObject values2 = o2.getJSONObject("values");

                            Map<String, Object> innerMap = values1.getInnerMap();
                            Map<String, Object> innerMap2 = values2.getInnerMap();
                            for (String key : innerMap.keySet()) {
                                Object o = innerMap.get(key);
                                Object o3 = innerMap2.get(key);
                                values1.put(key, o == null ? o3 : o);
                            }
                            o1.put("values", values1);
                        }
                    }
                }
            }
        }

        for (int j = 0; j < list.size(); j++) {
            JSONObject o1 = list.get(j);
            if (Objects.nonNull(o1)) {
                List<CellData> cellDataList1 = this.handlerRowData(columns, startRow, o1, dateQuery);
                cellDataList.addAll(cellDataList1);
            }
        }

        return cellDataList;
    }

    private List<CellData> dealSub3(List<String> columns, JSONArray data, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indexs = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            Date startTime = dayHourEach.get(i).getEndTime();
            String formatDateTime = DateUtil.getFormatDateTime(startTime, DateUtil.fullFormat);
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                String cell = columns.get(columnIndex);
                if (StringUtils.isNotBlank(cell)) {
                    for (int j = 0; j < data.size(); j++) {
                        JSONObject jsonObject = data.getJSONObject(j);
                        if (Objects.nonNull(jsonObject)) {
                            String calTime = jsonObject.getString("calTime");
                            if (formatDateTime.equals(calTime)) {
                                ExcelWriterUtil.addCellData(resultList, indexs, columnIndex, jsonObject.get(cell));
                                break;
                            }
                        }
                    }
                }
            }
            indexs++;
        }
        return resultList;
    }


    public List<CellData> handlerRowData(List<String> columns, int starRow, Map<String, Object> rowData, DateQuery dateQuery) {
        List<CellData> resultData = new ArrayList<>();
        // 忽略大小写
        CaseInsensitiveMap<String, Object> rowDataMap = new CaseInsensitiveMap<>(rowData);
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int row = starRow;
        //sampletime CaO SiO2
        for (int i = 0; i < dayHourEach.size(); i++) {
            DateQuery query = dayHourEach.get(i);
            Object o = rowDataMap.get("analysis");
            JSONObject object = (JSONObject) o;
            CaseInsensitiveMap<String, Object> map = new CaseInsensitiveMap<>(object);
            String sampletime = (String) map.get(columns.get(0));
            if (StringUtils.isNotBlank(sampletime)) {
                Date date1 = DateUtil.strToDate(sampletime, DateUtil.fullFormat);
                String formatDateTime = DateUtil.getFormatDateTime(date1, "yyyy-MM-dd HH:00:00");
                Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                String formatDateTime1 = DateUtil.getFormatDateTime(date1, "yyyy-MM-dd HH:mm:00");
                Date date2 = DateUtil.strToDate(formatDateTime1, DateUtil.fullFormat);
                Long betweenMin = DateUtil.getBetweenMin(date2, query.getStartTime());
                long abs = Math.abs(betweenMin);
                if (date.getTime() == query.getStartTime().getTime() || abs == 1) {
                    Object o1 = rowDataMap.get("values");
                    JSONObject values = (JSONObject) o1;
                    CaseInsensitiveMap<String, Object> map1 = new CaseInsensitiveMap<>(values);
                    Object caO = map1.get(columns.get(1));
                    Object siO2 = map1.get(columns.get(2));

                    ExcelWriterUtil.addCellData(resultData, row, 0, date1);
                    ExcelWriterUtil.addCellData(resultData, row, 1, caO);
                    ExcelWriterUtil.addCellData(resultData, row, 2, siO2);
                }
            }
            row++;
        }
        return resultData;
    }

    /**
     * @param url
     * @param param
     * @param col
     * @return
     */
    private String getTagValues(String url, DateQuery param, List<String> col) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("start", DateUtil.getFormatDateTime(param.getStartTime(), DateUtil.fullFormat));
        jsonObject.put("end", DateUtil.getFormatDateTime(param.getEndTime(), DateUtil.fullFormat));
        jsonObject.put("tagNames", col);
        String re1 = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        return re1;
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

    private String getUrl1(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/processCard/selectTargetVal";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/processCard/selectTargetVal";
        }
    }


    private String getUrl2(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/analysisValues/sampleTime";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/analysisValues/sampleTime";
        }
    }


    private String getUrl3(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/componentPrediction";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/componentPrediction";
        }
    }
}
