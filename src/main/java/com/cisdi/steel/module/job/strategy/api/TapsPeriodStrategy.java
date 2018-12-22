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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出铁作业月报
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/20 </P>
 *
 * @author leaf
 * @version 1.0
 */
@SuppressWarnings("Duplicates")
@Component
@Slf4j
public class TapsPeriodStrategy extends AbstractApiStrategy {
    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);
        List<Map<String, Object>> dataList = new ArrayList<>();
        //
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);

        List<String> bf = columns.stream().filter(v -> v.startsWith("BF")).collect(Collectors.toList());

        for (DateQuery dateQuery : queryList) {
            Map<String, Object> requestData = this.requestData(url, dateQuery, bf);
            dataList.add(requestData);
        }
        int index = 1;
        for (Map<String, Object> map : dataList) {
            map.put("index", index++);
        }
        List<CellData> cellDataList = this.loopRowData(dataList, columns);
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    private List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        int starRow = 1;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, data);
            resultData.addAll(rowData);
            starRow += 1;
        }
        return resultData;
    }

    private Map<String, Object> requestData(String urlPre, DateQuery dateQuery, List<String> bf) {
        String url = urlPre + "/taps/summary/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1000");
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(result);
        Object total = jsonObject.get("total");
        Map<String, Object> resultMap = new HashMap<>();
        if (Objects.isNull(total) || "0".equals(total.toString())) {
            return resultMap;
        }
        queryParam.put("pagesize", total.toString());
        result = httpUtil.get(url, queryParam);
        JSONObject object = JSONObject.parseObject(result);
        JSONArray jsonArray = object.getJSONArray("data");
        if (Objects.isNull(jsonArray)) {
            return resultMap;
        }
        int size = jsonArray.size();
        for (int i = 0; i < size; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (Objects.isNull(obj)) {
                continue;
            }
            Integer taphole = obj.getInteger("taphole");
            // 本次出铁量
            Object actweight = obj.get("actweight");
            resultMap.put("actweight" + taphole, actweight);

            // 铁口深度
            Object dephth = obj.get("dephth");
            resultMap.put("dephth" + taphole, dephth);

            // 铁水温度
            Object temp = obj.get("temp");
            resultMap.put("temp" + taphole, temp);


            String starttime = obj.getString("starttime");
            String endtime = obj.getString("endtime");
            if (StringUtils.isNotBlank(starttime) && StringUtils.isNotBlank(endtime)) {
                try {
                    Date startTime = new Date(Long.parseLong(starttime));
                    Date endTime = new Date(Long.parseLong(endtime));
                    Long betweenMin = DateUtil.getBetweenMin(endTime, startTime);
                    if(!"0".equals(betweenMin.toString())){
                        resultMap.put("time" + taphole, betweenMin.intValue());
                    }

                    // 处理第二部分数据
                    Map<String, Object> stringObjectMap = handlerLuoTie(urlPre, starttime, endtime, bf);
                    resultMap.putAll(stringObjectMap);

                    Map<String, BigDecimal> hm = handlerAnalysisValues(urlPre, "HM", endtime, starttime);
                    Map<String, BigDecimal> slag = handlerAnalysisValues(urlPre, "SLAG", endtime, starttime);
                    resultMap.put("hm", hm);
                    resultMap.put("slag", slag);
                } catch (Exception e) {
                    log.error("时间转换错误");
                }
            }
        }
        return resultMap;
    }

    private Map<String, BigDecimal> handlerAnalysisValues(String urlPre, String brandCode, String endTime, String startTime) {
        Map<String, BigDecimal> map = new CaseInsensitiveMap<>();
        String url2 = urlPre + "/analysisValues/sampletime";

        Map<String, String> queries = new HashMap<>();
        queries.put("brandcode", brandCode);
        queries.put("endtime", endTime);
        queries.put("starttime", startTime);
        queries.put("type", "LC");

        String childResult = httpUtil.get(url2, queries);
        if (StringUtils.isBlank(childResult)) {
            return getSampletime(urlPre, brandCode, endTime);
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
            return getSampletime(urlPre, brandCode, endTime);
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

    // 第二部分
    private Map<String, Object> handlerLuoTie(String urlPre, String starttime, String endtime, List<String> bf) {
        String url = urlPre + "/getTagValues/tagNamesInRange";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", starttime);
        jsonObject.put("endtime", endtime);
        jsonObject.put("tagnames", bf);
        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        if (StringUtils.isBlank(result)) {
            return new HashMap<>();
        }
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        Map<String, Object> map = new HashMap<>();
        for (String col : bf) {
            JSONObject values = obj.getJSONObject(col);
            if (Objects.nonNull(values)) {
                Double aDouble = handlerJSONObject(values);
                map.put(col, aDouble);
            }
        }
        return map;
    }

    private Double handlerJSONObject(JSONObject jsonObject) {
        List<Double> result = new ArrayList<>();
        Set<String> keySet = jsonObject.keySet();
        keySet.forEach(item -> {
            result.add(jsonObject.getDouble(item));
        });
        OptionalDouble average = result.stream().mapToDouble(Double::doubleValue).average();
        if (average.isPresent()) {
            return average.getAsDouble();
        }
        return null;
    }

    @Override
    public String getKey() {
        return "tapsPeriod";
    }
}
