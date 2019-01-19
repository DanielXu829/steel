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
                } else if ("_penmei3_month_day".equals(sheetName)) {
                    //喷吹压力以及罐号处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler2(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei6_month_day".equals(sheetName) || "_penmei7_month_day".equals(sheetName)) {
                    //换罐时间处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler3(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei8_month_day".equals(sheetName) || "_penmei9_month_day".equals(sheetName)) {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler4(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei10_month_day".equals(sheetName)) {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler5(version, columns, item, index);
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
        List<CellData> resultList = new ArrayList<>();

        int indes = index;
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        for (int i = 0; i < dayHourEach.size(); i++) {
            Object v = "";
            Object v1 = "";
            Object v2 = "";
            Object v3 = "";

            Object v4 = "";
            Object v5 = "";

            List<String> col = new ArrayList<>();
            col.add(columns.get(0));
            String re1 = getTagValues(dayHourEach.get(i).getQueryParam(), col, version);

            if (StringUtils.isNotBlank(re1)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1)) {
                    JSONObject datas = ob1.getJSONObject("data");
                    if (Objects.nonNull(datas)) {
                        JSONObject jsonObject = datas.getJSONObject(columns.get(0));
                        if (Objects.nonNull(jsonObject)) {
                            Map<String, Object> innerMap = jsonObject.getInnerMap();
                            Set<String> keySet = innerMap.keySet();
                            for (String key : keySet) {
                                Object o = innerMap.get(key);
                                if (Objects.nonNull(o)) {
                                    BigDecimal wac = (BigDecimal) o;
                                    if (wac.intValue() == 0) {
                                        v2 = wac.intValue();
                                        v3 = key;
                                        v = getTagValueByTime(key, version, columns.get(1));
                                        v1 = getTagValueByTime(key, version, columns.get(2));
                                        v4 = getTagValueByTime(key, version, columns.get(4));
                                        v5 = getTagValueByTime(key, version, columns.get(5));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ExcelWriterUtil.addCellData(resultList, indes, 0, v2);
            ExcelWriterUtil.addCellData(resultList, indes, 1, v);
            ExcelWriterUtil.addCellData(resultList, indes, 2, v1);
            ExcelWriterUtil.addCellData(resultList, indes, 3, v3);
            ExcelWriterUtil.addCellData(resultList, indes, 4, v4);
            ExcelWriterUtil.addCellData(resultList, indes, 5, v5);
            indes++;
        }


        return resultList;
    }

    protected List<CellData> mapDataHandler2(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            List<String> col = new ArrayList<>();
            col.add(columns.get(0));
            String re1 = getTagValues(dayHourEach.get(i).getQueryParam(), col, version);

            Object o = "";
            Object o1 = "";
            Object o2 = "";
            Object o3 = "";
            //罐号
            Set<Integer> set = new HashSet<>();
            String dateTime = null;
            boolean f = true;
            if (StringUtils.isNotBlank(re1)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1)) {
                    JSONObject data = ob1.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject jsonObject = data.getJSONObject(columns.get(0));
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

                            for (int j = 0; j < list.length; j++) {
                                Object mapObject = innerMap.get(list[j] + "");
                                if (Objects.nonNull(mapObject)) {
                                    BigDecimal ii = (BigDecimal) mapObject;
                                    int value = ii.intValue();
                                    if (f && value == 1) {
                                        dateTime = list[j] + "";
                                        f = false;
                                    }
                                    o = ii;
                                    set.add(value);
                                }
                            }
                        }
                    }
                }
            }
            if (Objects.nonNull(dateTime)) {
                o1 = getTagValueByTime(dateTime, version, columns.get(3));
                o2 = getTagValueByTime(dateTime, version, columns.get(4));
            }

            if (set.size() > 0) {

                Object[] objects = set.toArray();
                if (set.size() == 1) {
                    Integer m = (Integer) objects[0];
                    if (m.intValue() == 0) {
                        o3 = "2";
                    } else {
                        o3 = "1";
                    }
                } else if (set.size() == 2) {
                    Integer m = (Integer) objects[0];
                    if (m.intValue() == 0) {
                        o3 += "2";
                    } else {
                        o3 += "1";
                    }
                    Integer m1 = (Integer) objects[1];
                    if (m1.intValue() == 0) {
                        o3 += "2";
                    } else {
                        o3 += "1";
                    }
                } else if (set.size() == 3) {
                    Integer m = (Integer) objects[1];
                    if (m.intValue() == 0) {
                        o3 += "2";
                    } else {
                        o3 += "1";
                    }
                    Integer m1 = (Integer) objects[2];
                    if (m1.intValue() == 0) {
                        o3 += "2";
                    } else {
                        o3 += "1";
                    }
                }
            }


            ExcelWriterUtil.addCellData(resultList, indes, 0, o);
            ExcelWriterUtil.addCellData(resultList, indes, 3, o1);
            ExcelWriterUtil.addCellData(resultList, indes, 4, o2);
            ExcelWriterUtil.addCellData(resultList, indes, 5, o3);
            indes++;
        }

        return resultList;
    }

    private Object getTagValueByTime(String dateTime, String version, String column) {
        Object v = "";
        Map<String, String> map = new HashMap<>();
        map.put("time", dateTime);
        map.put("tagname", column);
        String re = httpUtil.get(getUrl1(version), map);
        if (StringUtils.isNotBlank(re)) {
            JSONObject ob = JSONObject.parseObject(re);
            if (Objects.nonNull(ob)) {
                JSONObject data = ob.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    v = data.get("val");
                }
            }
        }

        return v;
    }

    private String getTagValues(Map<String, String> param, List<String> col, String version) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", param.get("starttime"));
        jsonObject.put("endtime", param.get("endtime"));
        jsonObject.put("tagnames", col);
        String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
        return re1;
    }

    protected List<CellData> mapDataHandler3(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {


            List<String> col = new ArrayList<>();
            col.add(columns.get(0));
            String re1 = getTagValues(dayHourEach.get(i).getQueryParam(), col, version);

            List<String> col2 = new ArrayList<>();
            col2.add(columns.get(1));
            String re2 = getTagValues(dayHourEach.get(i).getQueryParam(), col2, version);

            Object o = "";
            Object o1 = "";
            if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                JSONObject ob2 = JSONObject.parseObject(re2);
                if (Objects.nonNull(ob1) && Objects.nonNull(ob2)) {
                    JSONObject data = ob1.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject jsonObject = data.getJSONObject(columns.get(0));
                        if (Objects.nonNull(jsonObject)) {
                            Map<String, Object> innerMap = jsonObject.getInnerMap();
                            for (String key : innerMap.keySet()) {
                                BigDecimal b1 = (BigDecimal) innerMap.get(key);
                                JSONObject data2 = ob2.getJSONObject("data");
                                if (Objects.nonNull(data2)) {
                                    JSONObject jsonObject2 = data2.getJSONObject(columns.get(1));
                                    if (Objects.nonNull(jsonObject2)) {
                                        Map<String, Object> innerMap2 = jsonObject2.getInnerMap();
                                        for (String key2 : innerMap2.keySet()) {
                                            BigDecimal b2 = (BigDecimal) innerMap2.get(key2);
                                            if (b1.doubleValue() == b2.doubleValue()) {
                                                o = key;
                                                o1 = key2;
                                            }
                                        }
                                    }
                                }
                            }
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

    protected List<CellData> mapDataHandler4(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("starttime", param.get("starttime"));
            jsonObject.put("endtime", param.get("endtime"));
            jsonObject.put("tagnames", columns.get(0));
            String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
            jsonObject.put("tagnames", columns.get(1));
            String re2 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());

            Object o = "";
            Object o1 = "";
            Object o2 = "";
            String dateTime = null;
            boolean f = false;
            if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                JSONObject ob2 = JSONObject.parseObject(re2);
                if (Objects.nonNull(ob1) && Objects.nonNull(ob2)) {
                    JSONObject data = ob1.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject tag1 = data.getJSONObject(columns.get(0));
                        if (Objects.nonNull(tag1)) {
                            Map<String, Object> innerMap = tag1.getInnerMap();
                            for (String key : innerMap.keySet()) {
                                BigDecimal b1 = (BigDecimal) innerMap.get(key);
                                JSONObject data2 = ob2.getJSONObject("data");
                                if (Objects.nonNull(data2)) {
                                    JSONObject tag2 = data2.getJSONObject(columns.get(1));
                                    if (Objects.nonNull(tag2)) {
                                        Map<String, Object> innerMap2 = tag2.getInnerMap();
                                        for (String key2 : innerMap2.keySet()) {
                                            BigDecimal b2 = (BigDecimal) innerMap2.get(key2);
                                            if (b1.intValue() == 0 && b2.intValue() == 0) {
                                                Long aLong = Long.valueOf(key);
                                                Long aLong1 = Long.valueOf(key2);
                                                dateTime = aLong > aLong1 ? key : key2;
                                                f = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (f) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            ExcelWriterUtil.addCellData(resultList, indes, 0, o);
            ExcelWriterUtil.addCellData(resultList, indes, 1, o1);
            if (Objects.nonNull(dateTime)) {
                o2 = getTagValueByTime(dateTime, version, columns.get(2));
            }
            ExcelWriterUtil.addCellData(resultList, indes, 2, o2);
            indes++;
        }

        return resultList;
    }

    protected List<CellData> mapDataHandler5(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("starttime", param.get("starttime"));
            jsonObject.put("endtime", param.get("endtime"));
            jsonObject.put("tagnames", columns.get(0));
            String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
            jsonObject.put("tagnames", columns.get(1));
            String re2 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());

            String dateTime = dealPart10_1(re1, re2, columns.get(0), columns.get(1));
            Object o2 = dealPart10_2(dateTime, version, columns.get(2));
            ExcelWriterUtil.addCellData(resultList, indes, 2, o2);

            jsonObject.put("tagnames", columns.get(3));
            re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
            jsonObject.put("tagnames", columns.get(4));
            re2 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());

            dateTime = dealPart10_1(re1, re2, columns.get(3), columns.get(4));
            Object o3 = dealPart10_2(dateTime, version, columns.get(5));
            ExcelWriterUtil.addCellData(resultList, indes, 5, o3);

            indes++;
        }

        return resultList;
    }

    private void dealData(List<Double> list, Object o) {
        if (Objects.nonNull(o)) {
            BigDecimal b = (BigDecimal) o;
            list.add(b.doubleValue());
        }
    }

    private String dealPart10(Map<String, Object> innerMap) {
        String tempDateTime1 = null;
        Set<String> keys = innerMap.keySet();
        Long[] list = new Long[keys.size()];
        int k = 0;
        for (String key : keys) {
            list[k] = Long.valueOf(key);
            k++;
        }
        Arrays.sort(list);
        int temp = -1;
        for (int j = 0; j < list.length; j++) {
            Object b1 = innerMap.get(list[j] + "");
            if (Objects.nonNull(b1)) {
                BigDecimal bb = (BigDecimal) b1;
                if (bb.intValue() == 1) {
                    temp = j;
                    break;
                }
            }
        }

        if (temp == 1) {
            for (int j = temp; j < list.length; j++) {
                Object b1 = innerMap.get(list[j] + "");
                if (Objects.nonNull(b1)) {
                    BigDecimal bb = (BigDecimal) b1;
                    if (bb.intValue() == 0) {
                        tempDateTime1 = list[j] + "";
                        break;
                    }
                }
            }
        }

        return tempDateTime1;
    }

    private String dealPart10_1(String re1, String re2, String col1, String col2) {
        String dateTime = null;
        if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
            JSONObject ob1 = JSONObject.parseObject(re1);
            JSONObject ob2 = JSONObject.parseObject(re2);
            if (Objects.nonNull(ob1) && Objects.nonNull(ob2)) {
                JSONObject data = ob1.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    JSONObject tag1 = data.getJSONObject(col1);
                    if (Objects.nonNull(tag1)) {
                        Map<String, Object> innerMap = tag1.getInnerMap();
                        String tempDateTime1 = dealPart10(innerMap);
                        if (Objects.nonNull(tempDateTime1)) {
                            JSONObject data2 = ob2.getJSONObject("data");
                            if (Objects.nonNull(data2)) {
                                JSONObject tag2 = data2.getJSONObject(col2);
                                if (Objects.nonNull(tag2)) {
                                    Map<String, Object> innerMap2 = tag2.getInnerMap();
                                    String tempDateTime2 = dealPart10(innerMap2);
                                    if (Objects.nonNull(tempDateTime2)) {
                                        Long aLong = Long.valueOf(tempDateTime1);
                                        Long bLong = Long.valueOf(tempDateTime2);
                                        dateTime = aLong > bLong ? tempDateTime1 : tempDateTime2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return dateTime;
    }

    private Object dealPart10_2(String dateTime, String version, String col) {
        Object v = null;
        if (Objects.nonNull(dateTime)) {
            //此时间往后5分钟的最大值
            Long time = Long.valueOf(dateTime);
            Date date = new Date(time);
            Object object1 = getTagValueByTime(dateTime, version, col);
            Object object2 = getTagValueByTime(DateUtil.addMinute(date, 1).getTime() + "", version, col);
            Object object3 = getTagValueByTime(DateUtil.addMinute(date, 2).getTime() + "", version, col);
            Object object4 = getTagValueByTime(DateUtil.addMinute(date, 3).getTime() + "", version, col);
            Object object5 = getTagValueByTime(DateUtil.addMinute(date, 4).getTime() + "", version, col);

            List<Double> list = new ArrayList<>();
            dealData(list, object1);
            dealData(list, object2);
            dealData(list, object3);
            dealData(list, object4);
            dealData(list, object5);

            Object[] array = list.toArray();
            Arrays.sort(array);
            v = array[array.length - 1];
        }
        return v;
    }

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    private String getUrl1(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }
}
