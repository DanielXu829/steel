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
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 全部同一个接口 策略
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <P>Date: 2019/12/16 </P>
 *
 * @version 1.0
 */
@Component
public class TagStrategy extends AbstractApiStrategy {

    @Override
    public String getKey() {
        return "tag";
    }

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    @Override
    public SheetRowCellData execute(Workbook workbook, Sheet sheet, List<DateQuery> queryList) {
        List<String> columnCells = PoiCustomUtil.getFirstRowCelVal(sheet);

        // 根据别名获取tag点名, 没有查到也留空
        for (int i = 0; i < columnCells.size(); i++) {
            if (columnCells.get(i).startsWith("ZP")) {
                String tagName = targetManagementMapper.selectTargetFormulaByTargetName(columnCells.get(i));
                if (StringUtils.isBlank(tagName)) {
                    columnCells.set(i, "");
                } else {
                    columnCells.set(i, tagName);
                }
            }
        }

        String version = PoiCustomUtil.getSheetCellVersion(workbook);
        String url = httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
        List<CellData> rowCellDataList = new ArrayList<>();
        int size = queryList.size();
        String sheetName = sheet.getSheetName();
        String[] split = sheetName.split("_");
        String type = split[2];
        String optionType = split[3];
        for (int rowNum = 0; rowNum < size; rowNum++) {
            DateQuery eachDate = queryList.get(rowNum);
            List<CellData> cellValInfoList = eachData(columnCells, url, eachDate.getQueryParam(), type, optionType, rowNum + 1);
            rowCellDataList.addAll(cellValInfoList);
        }

        return SheetRowCellData.builder()
                .workbook(workbook)
                .sheet(sheet)
                .cellDataList(rowCellDataList)
                .build();
    }

    private List<CellData> eachData(List<String> cellList, String url, Map<String, String> queryParam, String type, String optionType, int writeRow) {
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
                            List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(Long.valueOf(starttime)), new Date(Long.valueOf(endtime)));
                            if ("all".equals(optionType)) {
                                int rowIndex = 1;
                                for (int j = 0; j < dayEach.size(); j++) {
                                    DateQuery query = dayEach.get(j);
                                    Date queryStartTime = query.getStartTime();
                                    Date queryEndTime = query.getEndTime();
                                    for (int i = 0; i < list.length; i++) {
                                        Long tempTime = list[i];
                                        String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                                        Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                                        if (date.after(queryStartTime) && date.before(queryEndTime)) {
                                            Object o = data.get(tempTime + "");
                                            ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                                            break;
                                        }
                                    }
                                     rowIndex += 1;
                                }
                            } else if(optionType.indexOf("hour") > -1) {
                                handleWriteData(dayEach, list, data, resultList, writeRow, columnIndex);
                            } else {
                                handleWriteData(dayEach, list, data, resultList, writeRow, columnIndex);
                            }
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

    private void handleWriteData(List<DateQuery> dayEach, Long[] list, JSONObject data, List<CellData> resultList, int rowIndex, int columnIndex) {
        for (int j = 0; j < dayEach.size(); j++) {
            DateQuery query = dayEach.get(j);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();
            for (int i = 0; i < list.length; i++) {
                Long tempTime = list[i];
                String formatDateTime = DateUtil.getFormatDateTime(new Date(tempTime), "yyyy-MM-dd HH:00:00");
                Date date = DateUtil.strToDate(formatDateTime, DateUtil.fullFormat);
                if (date.after(queryStartTime) && date.before(queryEndTime)) {
                    Object o = data.get(tempTime + "");
                    ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, o);
                    break;
                }
            }
        }
    }

}
