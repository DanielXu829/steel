package com.cisdi.steel.module.job.a1.readwriter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.module.job.AbstractExcelReadWriter;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
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
 * <P>Date: 2018/11/8 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class ChutiezuoyeDayReadWriter extends AbstractExcelReadWriter {
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        List<Map<String, Object>> dataList = this.requestApiData(excelDTO.getDateQuery());
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        // 第一个sheet值
        Sheet sheet = this.getSheet(workbook, "_tap_day_each", excelDTO.getTemplate().getTemplatePath());
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        int starRow = 0;
        List<CellData> resultData = new ArrayList<>();
        for (Map<String, Object> rowData : dataList) {
            List<CellData> cellDataList = ExcelWriterUtil.handlerRowData(columns, starRow, rowData);
            resultData.addAll(cellDataList);
            starRow++;
        }
        ExcelWriterUtil.setCellValue(sheet, resultData);
        return workbook;
    }


    private List<Map<String, Object>> requestApiData(DateQuery dateQuery) {
        String url = httpProperties.getUrlApiGLOne() + "/taps/summary/period";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("pagenum", "1");
        queryParam.put("pagesize", "1");
        String result = httpUtil.get(url, queryParam);
        JSONObject jsonObject = JSON.parseObject(result);
        Object total = jsonObject.get("total");
        this.checkNull(total, "无法获取数据");
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
                    obj.put("sequnceno", i);
                    Map<String, Object> mapResult = new CaseInsensitiveMap<>();
                    mapResult.put("tapindex", obj);
                    String url2 = httpProperties.getUrlApiGLOne() + "/tap/analysis/" + tapid.toString();
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
