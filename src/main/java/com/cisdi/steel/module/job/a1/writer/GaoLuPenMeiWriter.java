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

            Map<String, String> param = dayHourEach.get(i).getQueryParam();
            String re1 = httpUtil.get(getUrl2(version, columns.get(0)), param);

            if (StringUtils.isNotBlank(re1)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1)) {
                    JSONArray datas = ob1.getJSONArray("data");
                    for (int j = 0; j < datas.size(); j++) {
                        JSONObject jsonObjects = datas.getJSONObject(j);
                        BigDecimal value = jsonObjects.getBigDecimal("value");
                        if (Objects.nonNull(value) && value.compareTo(BigDecimal.ZERO) == 0) {
                            Long datetime = jsonObjects.getLong("datetime");
                            v2 = datetime;

                            Map<String, String> map = new HashMap<>();
                            map.put("datetime", datetime.toString());
                            String re = httpUtil.get(getUrl1(version, columns.get(1)), map);
                            if (StringUtils.isNotBlank(re)) {
                                JSONObject ob = JSONObject.parseObject(re);
                                if (Objects.nonNull(ob)) {
                                    v = ob.getBigDecimal("value");
                                }
                            }

                            String res = httpUtil.get(getUrl1(version, columns.get(2)), map);
                            if (StringUtils.isNotBlank(res)) {
                                JSONObject obs = JSONObject.parseObject(res);
                                if (Objects.nonNull(obs)) {
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
            Object o = "";
            Object o1 = "";
            Object o2 = "";
            Object o3 = "";
            //罐号
            Set<Integer> set = new HashSet<>();
            Long dateTime = null;
            if (StringUtils.isNotBlank(re1)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                if (Objects.nonNull(ob1)) {
                    JSONArray data = ob1.getJSONArray("data");
                    for (int j = 0; j < data.size(); j++) {
                        JSONObject jsonObject = data.getJSONObject(j);
                        BigDecimal value = jsonObject.getBigDecimal("value");
                        if (Objects.nonNull(value)) {
                            Integer ii = value.intValue();
                            o = ii;
                            set.add(ii);
                            dateTime = jsonObject.getLong("datetime");
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

    private Object getTagValueByTime(Long dateTime, String version, String column) {
        Object v = "";
        Map<String, String> map = new HashMap<>();
        map.put("datetime", dateTime.toString());
        String re = httpUtil.get(getUrl1(version, column), map);
        if (StringUtils.isNotBlank(re)) {
            JSONObject ob = JSONObject.parseObject(re);
            if (Objects.nonNull(ob)) {
                v = ob.getBigDecimal("value");
            }
        }

        return v;
    }

    protected List<CellData> mapDataHandler3(String version, List<String> columns, DateQuery dateQuery, int index) {
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
                JSONObject ob2 = JSONObject.parseObject(re2);
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

                            if (value.doubleValue() == value2.doubleValue()) {
                                Long datetime1 = jsonObject2.getLong("datetime");
                                o = datetime;
                                o1 = datetime1;
                                f = true;
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
            String re1 = httpUtil.get(getUrl2(version, columns.get(0)), param);
            String re2 = httpUtil.get(getUrl2(version, columns.get(1)), param);

            Object o = "";
            Object o1 = "";
            Object o2 = "";
            Long dateTime = null;
            boolean f = false;
            if (StringUtils.isNotBlank(re1) && StringUtils.isNotBlank(re2)) {
                JSONObject ob1 = JSONObject.parseObject(re1);
                JSONObject ob2 = JSONObject.parseObject(re2);
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

                            if (value.doubleValue() == 0 && value2.doubleValue() == 0) {
                                dateTime = jsonObject2.getLong("datetime");
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
            if (Objects.nonNull(dateTime)) {
                o2 = getTagValueByTime(dateTime, version, columns.get(2));
            }
            ExcelWriterUtil.addCellData(resultList, indes, 2, o2);
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
