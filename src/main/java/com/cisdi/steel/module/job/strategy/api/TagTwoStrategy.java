package com.cisdi.steel.module.job.strategy.api;

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

import java.util.*;

/**
 * 全部同一个接口 策略
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/9 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Component
public class TagTwoStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "cache";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columnCells = PoiCustomUtil.getFirstRowCelVal(sheet);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/cache/getTagValuesByRange";
        List<CellData> rowCellDataList = new ArrayList<>();
        int size = queryList.size();
        String sheetName = sheet.getSheetName();
        String[] split = sheetName.split("_");
        String type = split[2];
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = queryList.get(rowNum);
            // 增加20秒时间
            Date startTime = eachDate.getStartTime();
            long endTime = startTime.getTime() + 20 * 1000;
            eachDate.setEndTime(new Date(endTime));

            List<CellData> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam(), rowNum);
            rowCellDataList.addAll(cellValInfoList);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, int rowNum) {
        int size = cellList.size();
        List<CellData> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String tagName = cellList.get(i);
            if (StringUtils.isBlank(tagName)) {
                continue;
            }
            Object cellValue = handlerRequestData(tagName, url, queryParam);
            CellData cellData = new CellData(rowNum + 1, i, cellValue);
            result.add(cellData);
        }
        return result;
    }

    /**
     * 处理获取每一个值
     *
     * @param tagName
     * @param url
     * @param queryParam
     * @return
     */
    private Object handlerRequestData(String tagName, String url, Map<String, String> queryParam) {
        String result = httpUtil.get(url + "/" + tagName, queryParam);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        JSONArray data = JSONObject.parseObject(result).getJSONArray("data");
        if (Objects.isNull(data)) {
            return null;
        }
        int size = data.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            JSONObject jsonObject = data.getJSONObject(0);
            return jsonObject.get("value");
        }
        List<Double> doubleList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            Double value = jsonObject.getDouble("value");
            doubleList.add(value);
        }
        OptionalDouble average = doubleList.stream().mapToDouble(Double::doubleValue).average();
        if (average.isPresent()) {
            return average.getAsDouble();
        } else {
            return null;
        }
    }

}
