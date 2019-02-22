package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/10 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TapStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "tap";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);

        List<CellData> cellDataList = new ArrayList<>();
        for (DateQuery dateQuery : queryList) {
            List<Map<String, Object>> dataList = this.requestApiData(url, dateQuery);
            cellDataList.addAll(this.loopRowData(dataList, columns));
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    /**
     * 循环遍历数据
     * 每一个 dataList 对应 多行数据
     *
     * @param dataList 数据
     * @param columns  对应的列
     * @return 结果
     */
    private List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        int starRow = 1;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            // 存储每一个
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, data);
            resultData.addAll(rowData);
            starRow++;
        }
        return resultData;
    }

    private List<Map<String, Object>> requestApiData(String urlPre, DateQuery dateQuery) {
        String url = urlPre + "/taps/report/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1");
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(result);
        Object total = jsonObject.get("total");
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (Objects.isNull(total) || "0".equals(total.toString())) {
            return resultList;
        }
        queryParam.put("pagesize", total.toString());
        result = httpUtil.get(url, queryParam);
        JSONObject object = JSONObject.parseObject(result);
        JSONArray jsonArray = object.getJSONArray("data");
        if (Objects.nonNull(jsonArray)) {
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject obj = jsonArray.getJSONObject(size - 1 - i);
                Object tapid = obj.get("tapid");
                if (Objects.nonNull(tapid)) {
                    Integer sequnceno = i + 1;
                    obj.put("sequnceno", sequnceno);
                    Map<String, Object> mapResult = new CaseInsensitiveMap<>();
                    String startTime = obj.getString("starttime");
                    String endTime = obj.getString("endtime");
                    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                        Date start = new Date(Long.parseLong(startTime));
                        Date end = new Date(Long.parseLong(endTime));
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(start);
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(end);
                        Long betweenMin = DateUtil.getBetweenMin(end, start);
//                        if (calendarStart.get(Calendar.DATE) != calendarEnd.get(Calendar.DATE)) {
//                            Calendar calendarNow = Calendar.getInstance();
//                            calendarNow.setTime(dateQuery.getRecordDate());
//                            // 开始时间不等于今天
//                            Date dateBeginTime = DateUtil.getDateBeginTime(end);
//                            Long bet1 = 0L;
//                            if (calendarEnd.get(Calendar.DATE) == calendarNow.get(Calendar.DATE)) {
//                                bet1 = DateUtil.getBetweenMin(end, dateBeginTime);
//                                // 理论
//                                handlerData(obj, betweenMin, bet1);
//                                obj.put("starttime", dateBeginTime.getTime());
//                                obj.put("endtime", end.getTime());
//                            } else {
//                                bet1 = DateUtil.getBetweenMin(dateBeginTime, start);
//                                handlerData(obj, betweenMin, bet1);
//                                obj.put("starttime", start.getTime());
//                                obj.put("endtime", dateBeginTime.getTime());
//                            }
//                            if (bet1.intValue() == 0) {
//                                continue;
//                            }
//                            obj.put("between", bet1.intValue());
//                        } else {
//                            obj.put("between", betweenMin.intValue());
//                        }
                        obj.put("between", betweenMin.intValue());
                        int day = calendarStart.get(Calendar.HOUR_OF_DAY);
                        int shift = 1;
                        if (day < 8) {
                            shift = 1;
                        } else if (day < 16) {
                            shift = 2;
                        } else if (day < 25) {
                            shift = 3;
                        }
                        mapResult.put("SHIFT", shift);
                    }
                    mapResult.put("tapindex", obj);
                    Map<String, BigDecimal> hm = handlerAnalysisValues(urlPre, "HM", obj);
                    Map<String, BigDecimal> SLAG = handlerAnalysisValues(urlPre, "SLAG", obj);
                    mapResult.put("HM", hm);
                    mapResult.put("SLAG", SLAG);
                    resultList.add(mapResult);
                }
            }
        }
        return resultList;
    }

    private void handlerData(JSONObject obj, Long betweenMin, Long bet1) {
        if (betweenMin == 0) {
            return;
        }
        // 理论
        BigDecimal theroyweight = obj.getBigDecimal("theroyweight");
        if (Objects.nonNull(theroyweight)) {
            BigDecimal multiply = theroyweight.multiply(new BigDecimal(bet1));
            BigDecimal divide = multiply.divide(new BigDecimal(betweenMin), 3, RoundingMode.HALF_UP);
            obj.put("theroyweight", divide);
        }
        // 实际
        BigDecimal actweight = obj.getBigDecimal("actweight");
        if (Objects.nonNull(actweight)) {
            BigDecimal divide = actweight.multiply(new BigDecimal(bet1))
                    .divide(new BigDecimal(betweenMin), 3, RoundingMode.HALF_UP);
            obj.put("actweight", divide);
        }
    }


    private Map<String, BigDecimal> handlerAnalysisValues(String urlPre, String brandCode, JSONObject obj) {
        Map<String, BigDecimal> map = new CaseInsensitiveMap<>();
        String url2 = urlPre + "/analysisValues/sampletime";
        String endtime = obj.getString("endtime");
        String starttime = obj.getString("starttime");
        if (StringUtils.isBlank(starttime) || StringUtils.isBlank(endtime)) {
            return map;
        }

        Map<String, String> queries = new HashMap<>();
        queries.put("brandcode", brandCode);
        queries.put("endtime", endtime);
        queries.put("starttime", starttime);
        queries.put("type", "LC");

        String childResult = httpUtil.get(url2, queries);
        if (StringUtils.isBlank(childResult)) {
            return getSampletime(urlPre, brandCode, endtime);
        }
        JSONObject childObject = JSON.parseObject(childResult);
        JSONArray jsonChildArray = childObject.getJSONArray("data");

        if (Objects.nonNull(jsonChildArray)) {
            int childSize = jsonChildArray.size();
            Map<String, List<BigDecimal>> result = new HashMap<>();
            for (int j = 0; j < childSize; j++) {
                JSONObject tapAnalysisJSONObject = jsonChildArray.getJSONObject(j);
                JSONObject values = tapAnalysisJSONObject.getJSONObject("values");
                if (Objects.nonNull(values)) {
                    Set<String> keySet = values.keySet();
                    keySet.forEach(item -> {
                        List<BigDecimal> value = result.get(item);
                        if (Objects.isNull(value)) {
                            value = new ArrayList<>();
                            value.add(values.getBigDecimal(item));
                            result.put(item, value);
                        } else {
                            value.add(values.getBigDecimal(item));
                        }
                    });
                }
            }

            result.forEach((k, v) -> {
                OptionalDouble average = v.stream().mapToDouble(BigDecimal::doubleValue).average();
                if (average.isPresent()) {
                    map.put(k, new BigDecimal(average.getAsDouble()).setScale(5, BigDecimal.ROUND_HALF_UP));
                } else {
                    map.put(k, new BigDecimal(0));
                }
            });
        }
        if (map.isEmpty()) {
            return getSampletime(urlPre, brandCode, endtime);
        }
        return map;
    }

    private Map<String, BigDecimal> getSampletime(String urlPre, String brandCode, String endTime) {
        Map<String, BigDecimal> map = new HashMap<>();
        String url = urlPre + "/analysisValue/sampletime/" + endTime;
        Map<String, String> queries = new HashMap<>();
        queries.put("brandcode", brandCode);
        queries.put("type", "LC");
        String s = httpUtil.get(url, queries);
        if (StringUtils.isBlank(s)) {
            return map;
        }
        JSONObject childObject = JSON.parseObject(s);
        if (Objects.isNull(childObject)) {
            return map;
        }
        JSONObject data = childObject.getJSONObject("data");
        if (Objects.isNull(data)) {
            return map;
        }
        JSONObject dataValues = data.getJSONObject("values");
        if (Objects.isNull(dataValues)) {
            return map;
        }
        Set<String> keySet = dataValues.keySet();
        keySet.forEach(item -> map.put(item, dataValues.getBigDecimal(item)));
        return map;
    }
}
