package com.cisdi.steel.module.job.strategy.api;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.SheetRowCellData;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
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
        String starttime = queryParam.get("starttime");
        jsonObject.put("starttime", starttime);
        String endtime = queryParam.get("endtime");
        jsonObject.put("endtime", endtime);
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
                            List<DateQuery> dayEach = DateQueryUtil.buildStartAndEndDayHourEach(new Date(Long.valueOf(starttime)), new Date(Long.valueOf(endtime)));
                            int rowIndex = 1;
                            for (int j = 0; j < dayEach.size(); j++) {
                                DateQuery query = dayEach.get(j);
                                Date recordDate = query.getStartTime();
                                for (int i = 0; i < list.length; i++) {
                                    Long tempTime = list[i];
                                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                                    if (date.getTime() == recordDate.getTime()) {
                                        Object o = data.get(tempTime + "");
                                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                                        break;
                                    }
                                }
                                rowIndex += 1;
                            }
//                            int size = list.length;
//                            for (int i = 0; i < size; i++) {
//                                Long key = list[i];
//                                Date date = new Date(key);
//                                Calendar calendar = Calendar.getInstance();
//                                calendar.setTime(date);
//                                int rowIndex = calendar.get(Calendar.HOUR_OF_DAY);
//                                if (rowIndex == 0) {
//                                    if (size > 1 && i == size - 1) {
//                                        rowIndex = 24;
//                                    } else {
//                                        continue;
//                                    }
//                                }
//                                Object o = data.get(key + "");
//                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
//                            }
                        } else if ("month".equals(type)) {
                            List<DateQuery> dayEach = DateQueryUtil.buildStartAndEndDayEach(new Date(Long.valueOf(starttime)), new Date(Long.valueOf(endtime)));
                            int rowIndex = 1;
                            for (int i = 0; i < dayEach.size(); i++) {
                                DateQuery query = dayEach.get(i);
                                Date recordDate = query.getRecordDate();
                                for (int j = 0; j < list.length; j++) {
                                    Long tempTime = list[j];
                                    String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd 00:00:00");
                                    Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                                    if (date.getTime() == recordDate.getTime()) {
                                        Object o = data.get(tempTime + "");
                                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                                        break;
                                    }
                                }
                                rowIndex += 1;
                            }
//                            for (Long key : list) {
//
//                                Date date = new Date(key);
//                                Calendar calendar = Calendar.getInstance();
//                                calendar.setTime(date);
//                                int rowIndex = calendar.get(Calendar.DATE);
//                                Object o = data.get(key + "");
//                                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
//                            }
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
