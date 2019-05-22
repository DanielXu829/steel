package com.cisdi.steel.module.job.a3.writer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
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
 * 熔剂燃料质量管控
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/13 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class RongjiWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
//        String version = PoiCustomUtil.getSheetCell(workbook, "_dictionary", 0, 1);
        DateQuery date = this.getDateQuery(excelDTO);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String version = "5.0";
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = sheet.getSheetName();
            String[] sheetSplit = sheetName.split("_");
            if (sheetSplit.length == 4) {
                //指定时间的最开始时间
                Date beginTime = DateUtil.getDateBeginTime(date.getRecordDate());
                // 获取的对应的策略
                List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, date.getRecordDate());
                List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
                int index = 1;
                String main1 = sheetSplit[1];
                if (main1.startsWith("6")) {
                    version = "6.0";
                }
                for (DateQuery item : dateQueries) {
                    List<CellData> cellDataList = this.mapDataHandler1(columns, item, index, sheetName, version, beginTime);
                    ExcelWriterUtil.setCellValue(sheet, cellDataList);
                }

            }
        }
        return workbook;
    }

    protected JSONObject getQueryParam1(DateQuery dateQuery) {
        JSONObject result = new JSONObject();
        result.put("start", dateQuery.getStartTime().getTime());
        result.put("end", dateQuery.getEndTime().getTime());
        return result;
    }

    protected List<CellData> mapDataHandler1(List<String> columns, DateQuery dateQuery, int index, String sheetName, String version, Date beginTime) {
        List<CellData> cellDataList = new ArrayList<>();
        JSONObject queryParam = this.getQueryParam1(dateQuery);
        queryParam.put("itemNames", columns);
        ArrayList<String> list = new ArrayList<>();
        String url = getUrl(version);
        if ("_5jiaofen_month_all".equals(sheetName)) {
            list.add("5#烧结用焦粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        } else if ("_6jiaofen_month_all".equals(sheetName)) {
            list.add("6#烧结用焦粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        } else if ("_5meifen_month_all".equals(sheetName)) {
            list.add("5#烧结用煤粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        } else if ("_6meifen_month_all".equals(sheetName)) {
            list.add("6#烧结用煤粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        } else if ("_5rongji_month_all".equals(sheetName)) {
            list.add("5#烧结用生石灰粉");
            list.add("5#烧结用白云石粉");
            list.add("5#烧结用石灰石粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        } else if ("_6rongji_month_all".equals(sheetName)) {
            list.add("6#烧结用生石灰粉");
            list.add("6#烧结用白云石粉");
            list.add("6#烧结用石灰石粉");
            queryParam.put("brandCodes", list);
            url = getUrl(version);
        }

        SerializeConfig serializeConfig = new SerializeConfig();
        String jsonString = JSONObject.toJSONString(queryParam, serializeConfig);
        String result = httpUtil.postJsonParams(url, jsonString);

        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject obj = JSONObject.parseObject(result);
        JSONArray data = obj.getJSONArray("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int size = data.size();

        Map map = new HashMap();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            Long clock = jsonObject.getLong("clock");
            String bz = "";
            if (Objects.nonNull(clock)) {
                Date date = new Date(clock);
                String hh = DateUtil.getFormatDateTime(date, "HH");
                Integer h = Integer.valueOf(hh);
                if (h.intValue() >= 0 && h < 8) {
                    bz = "夜班";
                } else if (h.intValue() >= 8 && h < 16) {
                    bz = "白班";
                } else if (h.intValue() >= 16 && h < 24) {
                    bz = "中班";
                }
            }

            JSONObject values = jsonObject.getJSONObject("values");
            Map<String, Object> innerMap = values.getInnerMap();
            if (innerMap.size() > 0) {
                ExcelWriterUtil.addCellData(cellDataList, index, 0, clock);
                ExcelWriterUtil.addCellData(cellDataList, index, 10, bz);
                for (int j = 1; j < columns.size(); j++) {
                    Object o = innerMap.get(columns.get(j));
                    ExcelWriterUtil.addCellData(cellDataList, index, j, o);
                    if ("_5jiaofen_month_all".equals(sheetName) || "_6jiaofen_month_all".equals(sheetName)) {
                        if (Objects.nonNull(clock) && clock.longValue() == beginTime.getTime()) {
                            Object v = o;
                            if (map.containsKey(columns.get(j))) {
                                Object o1 = map.get(columns.get(j));

                                BigDecimal t1 = BigDecimal.ZERO;
                                BigDecimal t2 = BigDecimal.ZERO;
                                if (v instanceof BigDecimal) {
                                    t1 = (BigDecimal) v;
                                } else if (v instanceof Integer) {
                                    t1 = new BigDecimal((Integer) v);
                                }

                                if (o1 instanceof BigDecimal) {
                                    t2 = (BigDecimal) o1;
                                } else if (o1 instanceof Integer) {
                                    t2 = new BigDecimal((Integer) o1);
                                }
                                v = t1.add(t2).divide(new BigDecimal(2), 6, BigDecimal.ROUND_HALF_UP);
                            }
                            if (Objects.nonNull(v)) {
                                map.put(columns.get(j), v);
                            }
                        }
                    }
                }
                index++;
            }
        }
        if ("_5jiaofen_month_all".equals(sheetName) || "_6jiaofen_month_all".equals(sheetName)) {
            for (int j = 1; j < columns.size(); j++) {
                ExcelWriterUtil.addCellData(cellDataList, 1, 13 + j, map.get(columns.get(j)));
            }
        }

        return cellDataList;
    }


    private String getUrl(String version) {
        if ("5.0".equals(version)) {
            return httpProperties.getUrlApiSJOne() + "/analysis/anaItemValues4";
        } else {
            // "6.0".equals(version) 默认
            return httpProperties.getUrlApiSJTwo() + "/analysis/anaItemValues4";
        }
    }
}
