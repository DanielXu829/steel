package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
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
        for (DateQuery dateQuery : queryList) {
            List<Map<String, Object>> dataList = this.requestApiData(url, dateQuery, sheet);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    private List<Map<String, Object>> requestApiData(String urlPre, DateQuery dateQuery, Sheet sheet) {
        String url = urlPre + "/hsstate";
        Map<String, String> queries = new HashMap<>();
        queries.put("begintime", dateQuery.getQueryStartTime().toString());
        queries.put("endtime", dateQuery.getQueryEndTime().toString());
        String s = httpUtil.get(url, queries);
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isBlank(s)) {
            return list;
        }
        String optionResult = httpUtil.get(urlPre + "/hsstatesteup");
        JSONObject optionObj = JSONObject.parseObject(optionResult);
        JSONArray options = optionObj.getJSONArray("data");


        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONArray data = jsonObject.getJSONArray("data");

        Map<Integer, Set<String>> columns = new HashMap<>();
        if (Objects.nonNull(data) && !data.isEmpty()) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                JSONObject obj = data.getJSONObject(i);
                // 获取状态
                Integer state = obj.getInteger("state");
                Map<String, Object> map = new HashMap<>();
                String optionVal = getOptions(options, state);
                map.put("state", optionVal);

                Integer no = obj.getInteger("");
                Long begintime = obj.getLong("begintime");
                Long endtime = obj.getLong("endtime");

                Set<String> tagNames = columns.get(no);
                if (Objects.isNull(tagNames)) {
                    List<String> rowCelVal = PoiCustomUtil.getRowCelVal(sheet, no);
                    tagNames = new HashSet<>();
                    for (String item : rowCelVal) {
                        if (StringUtils.isNotBlank(item)) {
                            String[] split = item.split("/");
                            tagNames.add(split[0]);
                        }
                    }
                    columns.put(no, tagNames);
                }
                Map<String, Object> result = handlerDingMing(urlPre, begintime, endtime, tagNames);
                map.putAll(result);
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
                    // 求平均值
                    doubleStream.average().ifPresent(v -> resultMap.put(cell + "/AVERAGE", v));
                    // 最大值
                    doubleStream.max().ifPresent(v -> resultMap.put(cell + "/MAX", v));
                    if (list.length > 1) {
                        resultMap.put(cell + "/start", data.getDouble(list[0] + ""));
                        resultMap.put(cell + "/end", data.getDouble(list[list.length - 1] + ""));
                    }
                }
            }
        }
        return resultMap;
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
