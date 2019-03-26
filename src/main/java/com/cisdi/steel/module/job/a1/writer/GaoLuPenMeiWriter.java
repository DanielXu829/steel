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
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.mapper.ReportIndexMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 高炉喷煤报表
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

    /**
     * 处理6高炉喷煤 关联上月数据值
     *
     * @param date
     */
    //@TODO
    private void dealSp(Date date) {
        String dd = DateUtil.getFormatDateTime(date, "dd");
        //判断今天是否是本月第一天
        if ("01".equals(dd)) {
            //获取上月日期
            Date months = DateUtil.addMonths(date, -1);

            //查询数据库中 上月最后一张报表

            //获取最后一张报表指定列最后一天的数据

            //将数据写入当前表中指定位置
        }
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
                    //正点余量
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler1(url, columns, item, index, version);
                        ExcelWriterUtil.setCellValue(sheet, cellDataList);
                        index += 24;
                    }
                } else if ("_penmei12_month_day".equals(sheetName)) {
                    //喷吹量
                    int index = 1;
                    for (DateQuery item : dateQueries) {
                        List<CellData> cellDataList = mapDataHandler12(url, columns, item, index, version);
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
                        List<CellData> cellDataList = mapDataHandler3_1(version, columns, item, index);
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
        Map<String, String> queryParam = dateQuery.getQueryParam();
        String result = getTagValues1(queryParam, columns, version);
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
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            String re1 = getTagValues(param, col, version, true);

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
//                                    if (value < 0.20) {
                                    if (Objects.isNull(temp)) {
                                        temp = value;
                                        tempTime = key;
                                    }
                                    //1.找到最小时间
                                    tempTime = temp < value ? tempTime : key;
                                    temp = temp < value ? temp : value;
//                                    }
                                }
                            }
                        }
                    }
                }
            }
            //v2 = dealPart(tempTime, version, columns.get(0), "s", -10, 10);
            //v2 = dealPenChuiLiang(columns.get(i + 8), dayHourEach.get(i).getQueryParam(), version);
//            if (Objects.nonNull(tempTime)) {
//                Long time = Long.valueOf(tempTime);
//                Date date = new Date(time);
//                v2 = getTagValueByTime(DateUtil.addMinute(date, -1).getTime() + "", version, columns.get(i + 8));
//            }


            if (Objects.isNull(tempTime)) {
                tempTime = param.get("endtime");
                v = getTagValueByTime(tempTime, version, columns.get(i + 1));
                v4 = getTagValueByTime(tempTime, version, columns.get(4));
                v5 = getTagValueByTime(tempTime, version, columns.get(5));
            }
            if (Objects.nonNull(tempTime)) {
                v3 = tempTime;
                v = getTagValueByTime(tempTime, version, columns.get(1));
                v1 = getTagValueByTime(tempTime, version, columns.get(2));
                v4 = getTagValueByTime(tempTime, version, columns.get(4));
                v5 = getTagValueByTime(tempTime, version, columns.get(5));
                v6 = getTagValueByTime(tempTime, version, columns.get(6));
            }


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

    protected List<CellData> mapDataHandler12(String url, List<String> columns, DateQuery dateQuery, int index, String version) {
        List<CellData> resultList = new ArrayList<>();

        int indes = index;
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        for (int i = 0; i < dayHourEach.size(); i++) {
            Object v = "";
            String tempTime = null;
            List<String> col = new ArrayList<>();
            col.add(columns.get(0));
            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            String re1 = getTagValues(param, col, version, false);

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
//                                    if (value < 0.20) {
                                    if (Objects.isNull(temp)) {
                                        temp = value;
                                        tempTime = key;
                                    }
                                    //1.找到最小时间
                                    tempTime = temp < value ? tempTime : key;
                                    temp = temp < value ? temp : value;
//                                    }
                                }
                            }
                        }
                    }
                }
            }
            //v2 = dealPart(tempTime, version, columns.get(0), "s", -10, 10);
            //v2 = dealPenChuiLiang(columns.get(i + 8), dayHourEach.get(i).getQueryParam(), version);

            if (Objects.isNull(tempTime)) {
                tempTime = param.get("endtime");
                v = getTagValueByTime(tempTime, version, columns.get(i + 1));
            }
            if (Objects.nonNull(tempTime)) {
                Long time = Long.valueOf(tempTime);
                Date date = new Date(time);
                v = getTagValueByTime(DateUtil.addMinute(date, 1).getTime() + "", version, columns.get(i + 1));
            }

            ExcelWriterUtil.addCellData(resultList, indes, 0, v);
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
            String re1 = getTagValues(dayHourEach.get(i).getQueryParam(), col, version, true);

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

    private String getTagValues(Map<String, String> param, List<String> col, String version, boolean flag) {
        JSONObject jsonObject = new JSONObject();
        //将时间全部往前推1个小时
        if (flag) {
            dealDate(param);
        }

        jsonObject.put("starttime", param.get("starttime"));
        jsonObject.put("endtime", param.get("endtime"));
        jsonObject.put("tagnames", col);
        String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
        return re1;
    }

    private String getTagValues1(Map<String, String> param, List<String> col, String version) {
        JSONObject jsonObject = new JSONObject();
        //将时间全部往前推1个小时

        String starttime = param.get("starttime");
        String endtime = param.get("endtime");

        Long aLong = Long.valueOf(starttime);
        Long bLong = Long.valueOf(endtime);

        Date sDate = new Date(aLong);
        Date date = DateUtil.addHours(sDate, -1);

        Date eDate = new Date(bLong);
        param.put("starttime", date.getTime() + "");
        param.put("endtime", eDate.getTime() + "");


        jsonObject.put("starttime", param.get("starttime"));
        jsonObject.put("endtime", param.get("endtime"));
        jsonObject.put("tagnames", col);
        String re1 = httpUtil.postJsonParams(getUrl(version), jsonObject.toJSONString());
        return re1;
    }

    private void dealDate(Map<String, String> param) {
        String starttime = param.get("starttime");
        String endtime = param.get("endtime");

        Long aLong = Long.valueOf(starttime);
        Long bLong = Long.valueOf(endtime);

        Date sDate = new Date(aLong);
        Date date = DateUtil.addHours(sDate, -1);

        Date eDate = new Date(bLong);
        Date date1 = DateUtil.addHours(eDate, -1);
        param.put("starttime", date.getTime() + "");
        param.put("endtime", date1.getTime() + "");
    }

    protected List<CellData> mapDataHandler3_1(String version, List<String> columns, DateQuery dateQuery, int index) {
        List<CellData> resultList = new ArrayList<>();
        List<DateQuery> dayHourEach = DateQueryUtil.buildDayHourEach(dateQuery.getRecordDate());
        int indes = index;
        for (int i = 0; i < dayHourEach.size(); i++) {
            List<String> col = new ArrayList<>();
            col.add(columns.get(0));
            String re1 = getTagValues(dayHourEach.get(i).getQueryParam(), col, version, true);
            List<Map<String, Object>> listData = new ArrayList<>();
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
                                    if (j == 0) {
                                        temp = value;
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(list[j] + "", value);
                                        listData.add(map);
                                    }
                                    if (temp != value) {
                                        temp = value;
                                        Map<String, Object> map = new HashMap<>();
                                        map.put(list[j] + "", value);
                                        listData.add(map);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String dateResult = new String();
            if (listData.size() > 0) {
                if (listData.size() != 1) {
                    listData.remove(0);
                }

                for (int k = 0; k < listData.size(); k++) {
                    Map<String, Object> stringObjectMap = listData.get(k);
                    Set<String> keySet = stringObjectMap.keySet();
                    for (String key : keySet) {
                        Long time = Long.valueOf(key);
                        Date date = new Date(time);
                        String formatDateTime = DateUtil.getFormatDateTime(date, "HH:mm");
                        dateResult += formatDateTime + "/";
                    }
                }
            }

            if (dateResult.endsWith("/")) {
                dateResult = dateResult.substring(0, dateResult.length() - 1);
            }
            ExcelWriterUtil.addCellData(resultList, indes, 0, dateResult);
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
            List<String> tagName1 = new ArrayList<>();
            tagName1.add(columns.get(0));
            String re1 = getTagValues(param, tagName1, version, true);

            Object o = "";
            Object o2 = "";
            BigDecimal tota = BigDecimal.ZERO;
            String dateTime = null;
            List<Long> dateList = null;
            if (StringUtils.isNotBlank(re1)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1)) {
                    JSONObject data = ob1.getJSONObject("data");
                    if (Objects.nonNull(data)) {
                        JSONObject tag1 = data.getJSONObject(columns.get(0));
                        if (Objects.nonNull(tag1)) {
                            Map<String, Object> innerMap = tag1.getInnerMap();
                            //dateTime = dealPart10(innerMap, true);
                            dateList = dealPart10_3(innerMap);

                        }
                    }
                }
            }


            if (Objects.nonNull(dateList) && dateList.size() > 0) {
                for (Long key : dateList) {
                    Object valueByTime = getTagValueByTime(key + "", version, columns.get(1));
                    if (Objects.nonNull(valueByTime)) {

                        if (valueByTime instanceof BigDecimal) {
                            BigDecimal bigDecimal = (BigDecimal) valueByTime;
                            tota = tota.add(bigDecimal);
                        } else if (valueByTime instanceof Integer) {
                            Integer integer = (Integer) valueByTime;
                            tota = tota.add(new BigDecimal(integer));
                        }
                    }
                }
            }

            ExcelWriterUtil.addCellData(resultList, indes, 1, tota);
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
            String re1 = getTagValues(param, tagName1, version, false);

            List<String> tagName2 = new ArrayList<>();
            tagName2.add(columns.get(1));
            String re2 = getTagValues(param, tagName2, version, false);

            String dateTime = dealPart10_1(re1, re2, columns.get(0), columns.get(1));
            Object o2 = dealPart(dateTime, version, columns.get(2), "m", 1, 5);
            ExcelWriterUtil.addCellData(resultList, indes, 2, o2);

            List<String> tagName3 = new ArrayList<>();
            tagName3.add(columns.get(3));
            re1 = getTagValues(param, tagName3, version, false);

            List<String> tagName4 = new ArrayList<>();
            tagName4.add(columns.get(4));
            re2 = getTagValues(param, tagName4, version, false);

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
            String re1 = getTagValues(param, tagName, version, true);
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
            if (dataList.size() == 0) {
                for (int m = 0; m < 16; m++) {
                    ExcelWriterUtil.addCellData(resultList, indes, m, "");
                }
            } else {
                for (int m = 0; m < dataList.size(); m++) {
                    ExcelWriterUtil.addCellData(resultList, indes, m, dataList.get(m));
                }
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

    private List<Long> dealPart10_3(Map<String, Object> innerMap) {
        List<Long> dateList = new ArrayList<>();
        Set<String> keys = innerMap.keySet();
        Long[] list = new Long[keys.size()];
        int k = 0;
        for (String key : keys) {
            list[k] = Long.valueOf(key);
            k++;
        }
        Arrays.sort(list, Collections.reverseOrder());

        for (int j = 0; j < list.length - 1; j++) {
            Object b1 = innerMap.get(list[j] + "");
            Object b2 = innerMap.get(list[j + 1] + "");
            if (Objects.nonNull(b1) && Objects.nonNull(b2)) {
                BigDecimal bb = (BigDecimal) b1;
                BigDecimal bb1 = (BigDecimal) b2;
                if (bb.intValue() == 0 && bb1.intValue() == 1) {
                    dateList.add(list[j]);
                }
            }
        }
        return dateList;
    }

    private String dealPart10(Map<String, Object> innerMap, boolean desc) {
        String tempDateTime1 = null;
        Set<String> keys = innerMap.keySet();
        Long[] list = new Long[keys.size()];
        int k = 0;
        for (String key : keys) {
            list[k] = Long.valueOf(key);
            k++;
        }
        int index1 = -1;
        int index2 = -1;
        if (desc) {
            index1 = 0;
            index2 = 1;
            Arrays.sort(list, Collections.reverseOrder());
        } else {
            index1 = 1;
            index2 = 0;
            Arrays.sort(list);
        }
        int temp = -1;
        for (int j = 0; j < list.length; j++) {
            Object b1 = innerMap.get(list[j] + "");
            if (Objects.nonNull(b1)) {
                BigDecimal bb = (BigDecimal) b1;
                if (bb.intValue() == index1) {
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
                    if (bb.intValue() == index2) {
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
                        String tempDateTime1 = dealPart10(innerMap, false);
                        if (Objects.nonNull(tempDateTime1)) {
                            JSONObject data2 = ob2.getJSONObject("data");
                            if (Objects.nonNull(data2)) {
                                JSONObject tag2 = data2.getJSONObject(col2);
                                if (Objects.nonNull(tag2)) {
                                    Map<String, Object> innerMap2 = tag2.getInnerMap();
                                    String tempDateTime2 = dealPart10(innerMap2, false);
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

    private String getUrl(String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

    private String getUrl1(String version) {
        return httpProperties.getGlUrlVersion(version) + "/tagValue/latest";
    }
}
