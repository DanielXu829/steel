package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.DoubleStream;

/**
 * 热风炉 日报
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/21 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class HsstateStrategy extends AbstractApiStrategy {
    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);
        List<CellData> cellDataList = new ArrayList<>();
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        for (DateQuery dateQuery : queryList) {
//            dateQuery = new DateQuery(new Date(1545321600000L), new Date(1545494400000L), new Date());
            List<Map<String, Object>> dataList = this.requestApiData(url, dateQuery, sheet);
            cellDataList.addAll(this.loopRowData(dataList, columns));
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    private List<CellData> loopRowData(List<Map<String, Object>> dataList, List<String> columns) {
        int starRow = 5;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            // 存储每一个
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, data);
            resultData.addAll(rowData);
            starRow++;
        }
        return resultData;
    }

    private List<Map<String, Object>> requestApiData(String urlPre, DateQuery dateQuery, Sheet sheet) {
        String url = urlPre + "/hsstate";
        Map<String, String> queries = new HashMap<>();
        queries.put("begintime", dateQuery.getQueryStartTime().toString());
        queries.put("endtime", dateQuery.getQueryEndTime().toString());
        String resultData = httpUtil.get(url, queries);
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(resultData)) {
            return list;
        }
        String optionResult = httpUtil.get(urlPre + "/hsstatesteup");
        JSONObject optionObj = JSONObject.parseObject(optionResult);
        JSONArray options = optionObj.getJSONArray("data");


        JSONObject jsonObject = JSONObject.parseObject(resultData);
        JSONArray data = jsonObject.getJSONArray("data");

        Map<Integer, Set<String>> columns = new HashMap<>();
        if (Objects.nonNull(data) && !data.isEmpty()) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                JSONObject obj = data.getJSONObject(i);
                // 获取状态
                Integer state = obj.getInteger("state");
                Map<String, Object> map = new CaseInsensitiveMap<>();
                String optionVal = getOptions(options, state);
                map.put("state", optionVal);
                Integer hsno = obj.getInteger("hsno");
                map.put("hsno", hsno);

                Long starttime = obj.getLong("starttime");
                Long endtime = obj.getLong("endtime");
                map.put("starttime", starttime);
                map.put("endtime", endtime);

                // 缓存列名
                Set<String> tagNames = columns.get(hsno);
                if (Objects.isNull(tagNames)) {
                    List<String> rowCelVal = PoiCustomUtil.getRowCelVal(sheet, hsno);
                    tagNames = new HashSet<>();
                    for (String item : rowCelVal) {
                        if (StringUtils.isNotBlank(item)) {
                            String[] split = item.split("/");
                            tagNames.add(split[0]);
                        }
                    }
                    columns.put(hsno, tagNames);
                }

                if (Objects.nonNull(starttime) && Objects.nonNull(endtime)) {
                    Map<String, Object> result = handlerDingMing(urlPre, starttime, endtime, tagNames);
                    long minTime = 60 * 1000;
                    long min = (endtime - starttime) / minTime;
                    map.put("intervalTime", min+"");
                    map.putAll(result);
                }
                list.add(map);
            }

        }
        return list;
    }


    private Map<String, Object> handlerDingMing(String urlPre, Long beginTime, Long endTime, Set<String> tagnames) {
        String url = urlPre + "/getTagValues/tagNamesInRange";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", beginTime.toString());
        jsonObject.put("endtime", endTime.toString());
        jsonObject.put("tagnames", tagnames);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        Map<String, Object> resultMap = new CaseInsensitiveMap<>();
        for (String cell : tagnames) {
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    Set<String> keySet = data.keySet();
                    List<Double> resultData = new ArrayList<>();
                    Long[] list = new Long[keySet.size()];
                    int k = 0;
                    for (String key : keySet) {
                        list[k] = Long.valueOf(key);
                        resultData.add(data.getDouble(key));
                        k++;
                    }
                    Arrays.sort(list);

                    DoubleStream doubleStream = resultData.stream().mapToDouble(Double::doubleValue);
                    JSONObject object = new JSONObject();

                    // 处理后的名称
                    String key = handlerCellName(cell);
                    resultMap.put(key,object);
                    // 求平均值
                    doubleStream.average().ifPresent(v -> {
                        object.put("avg", v);
                    });
                    // 最大值
                    DoubleStream doubleStreamMax = resultData.stream().mapToDouble(Double::doubleValue);
                    doubleStreamMax.max().ifPresent(v -> object.put("max", v));
                    if (list.length > 0) {
                        object.put("start", data.getDouble(list[0] + ""));
                        object.put("end", data.getDouble(list[list.length - 1] + ""));
                    }
                }
            }
        }
        return resultMap;
    }

    private String handlerCellName(String cell) {
        String[] split = cell.split("_");
        String key = split[split.length - 3];
        if (key.startsWith("TI08")) {
            return key.substring(key.length() - 3);
        }
        return key;
    }

    /**
     * 处理状态
     *
     * @param jsonArray
     * @param state
     * @return
     */
    private String getOptions(JSONArray jsonArray, Integer state) {
        int count = jsonArray.size();
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Integer state1 = jsonObject.getInteger("state");
            if (state.equals(state1)) {
                return jsonObject.getString("descr");
            }
        }
        return "";
    }

    @Override
    public String getKey() {
        return "hsstate";
    }
}
