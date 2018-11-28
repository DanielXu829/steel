package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            // 理论上来说一次获取了所有，循环只执行一次
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
        String url = urlPre + "/taps/summary/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1");
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(result);
        Object total = jsonObject.get("total");
        queryParam.put("pagesize", total.toString());
        result = httpUtil.get(url, queryParam);
        JSONObject object = JSONObject.parseObject(result);
        JSONArray jsonArray = object.getJSONArray("data");
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (Objects.nonNull(jsonArray)) {
            int size = jsonArray.size();
            for (int i = 0; i < size; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Object tapid = obj.get("tapid");
                if (Objects.nonNull(tapid)) {
                    Integer sequnceno = i + 1;
                    obj.put("sequnceno", sequnceno);
                    Map<String, Object> mapResult = new CaseInsensitiveMap<>();
                    mapResult.put("tapindex", obj);
                    String url2 = urlPre + "/tap/analysis/" + tapid.toString();
                    String childResult = httpUtil.get(url2);
                    JSONObject childObject = JSON.parseObject(childResult);
                    JSONArray jsonChildArray = childObject.getJSONArray("data");
                    if (Objects.nonNull(jsonChildArray)) {
                        int childSize = jsonChildArray.size();
                        for (int j = 0; j < childSize; j++) {
                            JSONObject tapAnalysisJSONObject = jsonChildArray.getJSONObject(j);
                            JSONObject tapAnalysis = tapAnalysisJSONObject.getJSONObject("tapAnalysis");
                            Object brandcode = tapAnalysis.get("brandcode");
                            if (Objects.nonNull(brandcode)) {
                                Map<String, Object> mapResult1 = new CaseInsensitiveMap<>();
                                JSONObject analysisValue = tapAnalysisJSONObject.getJSONObject("analysisValue");
                                JSONObject values = analysisValue.getJSONObject("values");
                                if ("HM".equals(brandcode.toString())) {
                                    mapResult1.put("HM", values);
                                } else if ("SLAG".equals(brandcode.toString())) {
                                    mapResult1.put("SLAG", values);
                                } else if ("TapValue".equals(brandcode.toString())) {
                                    mapResult1.put("TapValue", values);
                                }
                                resultList.add(mapResult1);
                            }
                        }

                    }
                    resultList.add(mapResult);
                }
            }
        }
        return resultList;
    }
}
