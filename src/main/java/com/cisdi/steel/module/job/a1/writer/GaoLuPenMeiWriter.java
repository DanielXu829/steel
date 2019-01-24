package com.cisdi.steel.module.job.a1.writer;

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
                }else if ("_penmei3_month_day".equals(sheetName)) {
                    //喷吹压力以及罐号处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler2(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                }
                else if ("_penmei6_month_day".equals(sheetName) || "_penmei7_month_day".equals(sheetName)) {
                    //换罐时间处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler3(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                }
                else if ("_penmei8_month_day".equals(sheetName) || "_penmei9_month_day".equals(sheetName)) {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler4(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei10_month_day".equals(sheetName)) {
                    //罐重处理
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler5(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei11_month_day".equals(sheetName)) {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler6(version, columns, item, index);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else {
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler(url, columns, item, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                }
            }
        }
        return workbook;
    }

    protected List<CellData> mapDataHandler(String url, List<String> columns, DateQuery dateQuery, int index, String version) {
        Map<String, String> queryParam = DateQueryUtil.getQueryParam(dateQuery, 0, 0, -1);
        String result = getTagValues(queryParam, columns, version);
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
            Object v6 = "";
            String tempTime = null;

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

                            Long[] list = new Long[keySet.size()];
                            int k = 0;
                            for (String key : keySet) {
                                list[k] = Long.valueOf(key);
                                k++;
                            }
                            Arrays.sort(list);

                            Double temp = null;

                            for (int j = 0; j < list.length; j++) {
                                String key = list[j] + "";
                                Object o = innerMap.get(key);
                                if (Objects.nonNull(o)) {
                                    BigDecimal wac = (BigDecimal) o;
                                    wac = wac.setScale(2, BigDecimal.ROUND_HALF_UP);
                                    double value = wac.doubleValue();
                                    if (value < 0.15) {
                                        if (Objects.isNull(temp)) {
                                            temp = value;
                                            tempTime = key;
                                        }
                                        //1.找到最小时间
                                        tempTime = temp < value ? tempTime : key;
                                        temp = temp < value ? temp : value;
//                                        v2 = wac.intValue();
                                        v3 = key;
                                        v = getTagValueByTime(key, version, columns.get(1));
                                        v1 = getTagValueByTime(key, version, columns.get(2));
                                        v4 = getTagValueByTime(key, version, columns.get(4));
                                        v5 = getTagValueByTime(key, version, columns.get(5));
                                        v6 = getTagValueByTime(key, version, columns.get(6));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            v2 = dealPart(tempTime, version, columns.get(0), "s", -10, 10);
            ExcelWriterUtil.addCellData(resultList, indes, 0, v2);
            ExcelWriterUtil.addCellData(resultList, indes, 1, v);
            ExcelWriterUtil.addCellData(resultList, indes, 2, v1);
            ExcelWriterUtil.addCellData(resultList, indes, 3, v3);
            ExcelWriterUtil.addCellData(resultList, indes, 4, v4);
            ExcelWriterUtil.addCellData(resultList, indes, 5, v5);
            ExcelWriterUtil.addCellData(resultList, indes, 6, v6);
            indes++;
        }


        return resultList;
    }

    private Object dealPart(String dateTime, String version, String col, String type, int batch, int num) {
        Object v = null;
        if (Objects.nonNull(dateTime)) {
            Long time = Long.valueOf(dateTime);
            Date date = new Date(time);

            //分钟
            List<Double> list = new ArrayList<>();
            if ("m".equals(type)) {
                for (int i = 0; i < num; i++) {
                    Object object = getTagValueByTime(DateUtil.addMinute(date, i * batch).getTime() + "", version, col);
                    dealData(list, object);
                }
                //秒
            } else if ("s".equals(type)) {
                for (int i = 0; i < num; i++) {
                    Object object = getTagValueByTime(DateUtil.addSecond(date, i * batch).getTime() + "", version, col);
                    dealData(list, object);
                }
            }

            Object[] array = list.toArray();
            Arrays.sort(array);
            if (list.size() > 0) {
                v = array[array.length - 1];
            }
        }
        return v;
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
            List<Integer> set = new ArrayList<>();
            String dateTime = null;

            int temp = -1;

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
                                    o = ii;
                                    dateTime = list[j] + "";
                                    if (j == 0) {
                                        temp = value;
                                        set.add(value);
                                    }
                                    if (temp != value) {
                                        temp = value;
                                        set.add(value);
                                    }
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
                if (set.size() != 1) {
                    set.remove(0);
                }
                for (int k = 0; k < set.size(); k++) {
                    int vv = set.get(k);
                    if (vv == 0) {
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

        Object v = null;
        if (StringUtils.isNotBlank(dateTime)) {
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

            Long o = null;
            Long o1 = null;
            String huanguan = new String();

            boolean f = true;

            if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                JSONObject ob2 = JSONObject.parseObject(re2);
                if (Objects.nonNull(ob1) && Objects.nonNull(ob2)) {
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
                                BigDecimal b1 = (BigDecimal) innerMap.get(list[j] + "");
                                JSONObject data2 = ob2.getJSONObject("data");
                                if (Objects.nonNull(data2)) {
                                    JSONObject jsonObject2 = data2.getJSONObject(columns.get(1));
                                    if (Objects.nonNull(jsonObject2)) {
                                        Map<String, Object> innerMap2 = jsonObject2.getInnerMap();

                                        Set<String> keys2 = innerMap2.keySet();
                                        Long[] list2 = new Long[keys2.size()];
                                        int k2 = 0;
                                        for (String key : keys2) {
                                            list2[k2] = Long.valueOf(key);
                                            k2++;
                                        }
                                        Arrays.sort(list2);

                                        for (int m = 0; m < list2.length; m++) {
                                            BigDecimal b2 = (BigDecimal) innerMap2.get(list2[m] + "");
                                            BigDecimal subtract = b1.subtract(b2);
                                            subtract = subtract.setScale(2, BigDecimal.ROUND_HALF_UP);
                                            if (subtract.doubleValue() >= -0.15 && subtract.doubleValue() <= 0.15) {
                                                o = Long.valueOf(list[j]);
                                                o1 = Long.valueOf(list2[m]);
//
//                                                Date date = new Date(o);
//                                                String formatDateTime = DateUtil.getFormatDateTime(date, "HH:mm:ss");
//                                                Date date1 = new Date(o1);
//                                                String formatDateTime1 = DateUtil.getFormatDateTime(date1, "HH:mm:ss");
//
//                                                huanguan += (o > o1 ? formatDateTime : formatDateTime1) + "/";
                                                f = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (!f) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

//            if (huanguan.endsWith("/")) {
//                huanguan = huanguan.substring(0, huanguan.length() - 2);
//            }

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
            List<String> tagName1 = new ArrayList<>();
            tagName1.add(columns.get(0));
            jsonObject.put("tagnames", tagName1);
            String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
            List<String> tagName2 = new ArrayList<>();
            tagName2.add(columns.get(1));
            jsonObject.put("tagnames", tagName2);
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
            List<String> tagName1 = new ArrayList<>();
            tagName1.add(columns.get(0));
            String re1 = getTagValues(param, tagName1, version);

            List<String> tagName2 = new ArrayList<>();
            tagName2.add(columns.get(1));
            String re2 = getTagValues(param, tagName2, version);

            String dateTime = dealPart10_1(re1, re2, columns.get(0), columns.get(1));
            Object o2 = dealPart(dateTime, version, columns.get(0), "m", 1, 5);
            ExcelWriterUtil.addCellData(resultList, indes, 2, o2);

            List<String> tagName3 = new ArrayList<>();
            tagName3.add(columns.get(3));
            re1 = getTagValues(param, tagName3, version);

            List<String> tagName4 = new ArrayList<>();
            tagName4.add(columns.get(4));
            re2 = getTagValues(param, tagName4, version);

            Object o6 = getTagValueByTime(dateTime, version, columns.get(6));
            ExcelWriterUtil.addCellData(resultList, indes, 6, o6);

            dateTime = dealPart10_1(re1, re2, columns.get(3), columns.get(4));
            Object o3 = dealPart(dateTime, version, columns.get(5), "m", 1, 5);
            ExcelWriterUtil.addCellData(resultList, indes, 5, o3);

            Object o7 = getTagValueByTime(dateTime, version, columns.get(6));
            ExcelWriterUtil.addCellData(resultList, indes, 7, o7);

            indes++;
        }

        return resultList;
    }

    protected List<CellData> mapDataHandler6(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            List<Object> dataList = new ArrayList<>();
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            List<String> tagName = new ArrayList<>();
            tagName.add(columns.get(0));
            String re1 = getTagValues(param, tagName, version);
            if (StringUtils.isNotBlank(re1)) {
                JSONObject object = JSONObject.parseObject(re1);
                if (Objects.nonNull(object)) {
                    JSONObject data = object.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject tag = data.getJSONObject(columns.get(0));
                        if (Objects.nonNull(tag)) {
                            Map<String, Object> innerMap = tag.getInnerMap();
                            Set<String> keys = innerMap.keySet();
                            Long[] list = new Long[keys.size()];
                            int k = 0;
                            for (String key : keys) {
                                list[k] = Long.valueOf(key);
                                k++;
                            }
                            Arrays.sort(list);
                            Object temp = "";
                            for (int j = 0; j < list.length; j++) {
                                Object o = innerMap.get(list[j] + "");
                                if (!temp.equals(o)) {
                                    temp = o;
                                    if (j != 0) {
                                        dataList.add(list[j]);
                                        dataList.add(o);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int m = 0; m < dataList.size(); m++) {
                ExcelWriterUtil.addCellData(resultList, indes, m, dataList.get(m));
            }
            indes++;
        }

        return resultList;
    }

    private void dealData(List<Double> list, Object o) {
        if (Objects.nonNull(o)) {
            if (o instanceof Integer) {
                Integer b = (Integer) o;
                list.add(b.doubleValue());
            } else if (o instanceof BigDecimal) {
                BigDecimal b = (BigDecimal) o;
                list.add(b.doubleValue());
            }

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

        if (temp != -1) {
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
            if (list.size() > 0) {
                v = array[array.length - 1];
            }
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
