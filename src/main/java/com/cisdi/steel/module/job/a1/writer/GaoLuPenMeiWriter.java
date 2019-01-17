package com.cisdi.steel.module.job.a1.writer;

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
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class GaoLuPenMeiWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        return this.getMapHandler1(getUrl(version), excelDTO, version);
    }


    protected Workbook getMapHandler1(String url, WriterExcelDTO excelDTO, String version) {
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
                if ("_penmei2_month_day".equals(sheetName)) {
                    //喷吹量处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler1(url, columns, item, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei6_month_day".equals(sheetName) || "_penmei7_month_day".equals(sheetName)) {
                    //换罐时间处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler2(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler(url, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                }


            }
        }
        return workbook;
    }

    @Override
    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int index) {
        Map<String, String> queryParam = DateQueryUtil.getQueryParam(dateQuery, 0, 0, -1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", columns);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
            int indexs = index;
            String cell = columns.get(columnIndex);
            JSONObject data = obj.getJSONObject(cell);
            List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
            for (int i = 0; i < dayHourEach.size(); i++) {
                Object v = "";
                if (Objects.nonNull(data)) {
                    Date startTime = dayHourEach.get(i).getStartTime();
                    v = data.get(startTime.getTime() + "");
                }
                ExcelWriterUtil.addCellData(resultList, indexs++, columnIndex, v);
            }

        }
        return resultList;
    }

    protected List<CellData> mapDataHandler1(String url, List<String> columns, DateQuery dateQuery, int index, String version) {
        ArrayList<String> list = new ArrayList<>();
        list.add(columns.get(0));
        Map<String, String> queryParam = DateQueryUtil.getQueryParam(dateQuery, 0, 0, -1);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", list);


        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        String cell = columns.get(0);
        JSONObject data = obj.getJSONObject(cell);

        int indes = index;
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        for (int i = 0; i < dayHourEach.size(); i++) {
            Object v = "";
            Object v1 = "";
            Object v2 = "";
            if (Objects.nonNull(data)) {
                Map<String, Object> innerMap = data.getInnerMap();
                for (String key : innerMap.keySet()) {
                    Object o = innerMap.get(key);
                    v2 = o;
                    if (Objects.nonNull(o)) {
                        BigDecimal b = (BigDecimal) o;
                        if (b.intValue() == 0) {
                            Map<String, String> map = new HashMap<>();
                            map.put("datetime", key);
                            String re = httpUtil.get(getUrl1(version, columns.get(1)), map);
                            if (StringUtils.isNotBlank(re)) {
                                JSONObject ob = JSONObject.parseObject(re);
                                if (Objects.nonNull(ob)) {
                                    v = ob.getBigDecimal("value");
                                }
                            }

                            String re1 = httpUtil.get(getUrl1(version, columns.get(2)), map);
                            if (StringUtils.isNotBlank(re1)) {
                                JSONObject ob1 = JSONObject.parseObject(re1);
                                if (Objects.nonNull(ob1)) {
                                    v1 = ob1.getBigDecimal("value");
                                }
                            }
                        }
                    }
                }
            }
            ExcelWriterUtil.addCellData(resultList, indes, 0, v2);
            ExcelWriterUtil.addCellData(resultList, indes, 1, v);
            ExcelWriterUtil.addCellData(resultList, indes, 2, v1);
            indes++;
        }


        return resultList;
    }

    protected List<CellData> mapDataHandler2(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            String re1 = httpUtil.get(getUrl2(version, columns.get(0)), param);
            String re2 = httpUtil.get(getUrl2(version, columns.get(1)), param);

            Object o = "";
            Object o1 = "";
            boolean f = false;
            if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                JSONObject ob2 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1) && Objects.nonNull(ob2)) {
                    JSONArray data = ob1.getJSONArray("data");
                    for (int j = 0; j < data.size(); j++) {
                        JSONObject jsonObject = data.getJSONObject(j);
                        BigDecimal value = jsonObject.getBigDecimal("value");
                        Long datetime = jsonObject.getLong("datetime");
                        JSONArray data2 = ob2.getJSONArray("data");
                        for (int m = 0; m < data2.size(); m++) {
                            JSONObject jsonObject2 = data2.getJSONObject(j);
                            BigDecimal value2 = jsonObject2.getBigDecimal("value");

                            if (value.compareTo(value2) == 0) {
                                Long datetime1 = jsonObject2.getLong("datetime");
                                o = datetime;
                                o1 = datetime1;
                                f = true;
                                break;
                            }
                        }

                        if (f) {
                            break;
                        }
                    }
                }
            }
            ExcelWriterUtil.addCellData(resultList, indes, 0, o);
            ExcelWriterUtil.addCellData(resultList, indes, 1, o1);
            indes++;
        }

        return resultList;
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    private String getUrl1(String version, String tagName) {
        return httpProperties.getGlUrlVersion(version) + "/cache/getTagValueByTime/" + tagName;
    }

    private String getUrl2(String version, String tagName) {
        return httpProperties.getGlUrlVersion(version) + "/cache/getTagValuesByRange/" + tagName;
    }
}
