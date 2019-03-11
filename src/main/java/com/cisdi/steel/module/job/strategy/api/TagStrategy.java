package com.cisdi.steel.module.job.strategy.api;

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
public class TagStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "tag";
    }

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columnCells = PoiCustomUtil.getFirstRowCelVal(sheet);
        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
        List<CellData> rowCellDataList = new ArrayList<>();
        int size = queryList.size();
        String sheetName = sheet.getSheetName();
        String[] split = sheetName.split("_");
        String type = split[2];
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = queryList.get(rowNum);
            List<CellData> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam(), type);
            rowCellDataList.addAll(cellValInfoList);
        }
        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, String type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("starttime", queryParam.get("starttime"));
        jsonObject.put("endtime", queryParam.get("endtime"));
        jsonObject.put("tagnames", cellList);

        String result = httpUtil.postJsonParams(url, jsonObject.toJSONString());
        JSONObject obj = JSONObject.parseObject(result);
        obj = obj.getJSONObject("data");
        List<CellData> resultList = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < cellList.size(); columnIndex++) {
            String cell = cellList.get(columnIndex);
            if (StringUtils.isNotBlank(cell)) {
                JSONObject data = obj.getJSONObject(cell);
                if (Objects.nonNull(data)) {
                    Set<String> keys = data.keySet();
                    Long[] list = new Long[keys.size()];
                    int k = 0;
                    for (String key : keys) {
                        list[k] = Long.valueOf(key);
                        k++;
                    }
                    // 按照顺序排序
                    Arrays.sort(list);
                    if (StringUtils.isNotBlank(type)) {
                        if ("day".equals(type)) {
                            int size = list.length;
                            for (int i = 0; i < size; i++) {
                                Long key = list[i];
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.HOUR_OF_DAY);
                                if (rowIndex == 0) {
                                    if (i == size - 1) {
                                        rowIndex = 24;
                                    } else {
                                        continue;
                                    }
                                }
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        } else if ("month".equals(type)) {
                            for (Long key : list) {
                                Date date = new Date(key);
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(date);
                                int rowIndex = calendar.get(Calendar.DATE);
                                Object o = data.get(key + "");
                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                            }
                        }
                    } else {
                        int rowIndex = 1;
                        for (Long key : list) {
                            Object o = data.get(key + "");
                            ExcelWriterUtil.addCellData(resultList, rowIndex++, columnIndex, o);
                        }
                    }

                }
            }
        }
        return resultList;
    }
}
