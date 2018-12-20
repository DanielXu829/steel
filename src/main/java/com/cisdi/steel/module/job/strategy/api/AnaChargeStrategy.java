package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 高炉成分分析
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/20 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class AnaChargeStrategy extends AbstractApiStrategy {
    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columns = PoiCustomUtil.getFirstRowCelVal(sheet);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version);
        List<CellData> cellDataList = new ArrayList<>();
        int starRow = 1;
        for (DateQuery dateQuery : queryList) {
            Map<String, Object> dataList = this.requestApiData(url, dateQuery, columns);
            List<CellData> rowData = ExcelWriterUtil.handlerRowData(columns, starRow, dataList);
            cellDataList.addAll(rowData);
            starRow++;
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(cellDataList)
                .build();
    }

    private Map<String, Object> requestApiData(String urlPre, DateQuery dateQuery, List<String> columns) {
        String url = urlPre + "/anaChargeValue/range";
        Map<String, String> queryParam = dateQuery.getQueryParam();
        queryParam.put("granularity", "hour");
        queryParam.put("type", "LC");
        Map<String, Object> map = new HashMap<>();
        for (String column : columns) {
            if (StringUtils.isBlank(column)) {
                continue;
            }
            queryParam.put("category", column);
            String s = httpUtil.get(url, queryParam);
            if (StringUtils.isBlank(s)) {
                continue;
            }
            JSONObject jsonObject = JSON.parseObject(s);
            JSONArray data = jsonObject.getJSONArray("data");
            List<BigDecimal> result = new ArrayList<>();
            if (Objects.nonNull(data)) {
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject values = data.getJSONObject(i);
                    if (Objects.nonNull(values)) {
                        JSONObject val = values.getJSONObject("values");
                        if (Objects.nonNull(val)) {
                            BigDecimal batchMass = val.getBigDecimal("batchMass");
                            result.add(batchMass);
                        }

                    }
                }
            }
            OptionalDouble average = result.stream().mapToDouble(BigDecimal::doubleValue).average();
            if (average.isPresent()) {
                map.put(column, new BigDecimal(average.getAsDouble()).setScale(4, BigDecimal.ROUND_HALF_UP));
            }
        }
        return map;
    }

    @Override
    public String getKey() {
        return "anaCharge";
    }
}
