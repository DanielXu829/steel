package com.cisdi.steel.module.job.gl.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 8高炉TRT工艺能耗统计表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2020 </p>
 * <P>Date: 2020/08/11</P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class TRTGongYiNengHaoTongJiWriter extends BaseGaoLuWriter {
    // 标记行
    private static int dataBeginRowNum = 4;
    /**
     * @param excelDTO 数据
     * @return
     */
    @Override
    public Workbook excelExecute(WriterExcelDTO excelDTO) {
        Workbook workbook = this.getWorkbook(excelDTO.getTemplate().getTemplatePath());
        String version = "8.0";
        try {
            version = PoiCustomUtil.getSheetCellVersion(workbook);
        } catch (Exception e) {
            log.error("在模板中获取version失败", e);
        }

        String queryUrl = getUrlTagNamesInRange(version);

        // 动态报表生成的模板默认取第二个sheet。
        String sheetName = "_gongyinenghao_day_hour";
        Sheet tagSheet = workbook.getSheet(sheetName);
        Sheet mainSheet = workbook.getSheetAt(0);
        DateQuery query = this.getDateQuery(excelDTO);
        // 22点到22点
        DateQuery dateQuery = DateQueryUtil.buildDayAheadTwoHour(query.getRecordDate());
        // 22-23，23-24， 24-1 ... 21-22
        List<DateQuery> dateQueries = DateQueryUtil.buildDayHourOneEach(dateQuery.getStartTime(), dateQuery.getEndTime());
        // 直接拿到tag点名, 无需根据别名再去获取tag点名
        List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(tagSheet);
        for (int rowNum = 0; rowNum < dateQueries.size(); rowNum++) {
            List<CellData> cellDataList = handleEachRowData(tagNames, queryUrl, dateQueries.get(rowNum), rowNum + 1);
            ExcelWriterUtil.setCellValue(tagSheet, cellDataList);
        }
        Date currentDate = query.getRecordDate();
        ExcelWriterUtil.replaceCurrentDateInTitle(mainSheet, "%当前日期%", currentDate);
        return workbook;
    }

    /**
     * 处理每行数据
     *
     * @param tagFormulas
     * @param queryUrl
     * @param queryParam
     * @param rowIndex
     * @return
     */
    private List<CellData> handleEachRowData(List<String> tagNames, String queryUrl, DateQuery dateQuery, int rowIndex) {
        // 批量查询tag value
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagNames);
        String result = httpUtil.postJsonParams(queryUrl, JSON.toJSONString(tagQueryParam));

        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
            Map<String, LinkedHashMap<Long, Double>> tagValueMaps = tagValueMapDTO.getData();
            // 获取targetManagement map

            for (int columnIndex = 0; columnIndex < tagNames.size(); columnIndex++) {
                String targetName = tagNames.get(columnIndex);
                if (StringUtils.isNotBlank(targetName)) {
                    Map<Long, Double> tagValueMap = tagValueMaps.get(targetName);
                    if (Objects.nonNull(tagValueMap)) {
                        List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(dateQuery.getQueryStartTime()), new Date(dateQuery.getQueryEndTime()));
                        handleEachCellData(dayEach, tagValueMap, resultList, rowIndex, columnIndex, targetName);
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 处理每个单元格数据
     *
     * @param dayEach
     * @param tagValueMap
     * @param resultList
     * @param rowIndex
     * @param columnIndex
     * @param tag
     */
    private void handleEachCellData(List<DateQuery> dayEach, Map<Long, Double> tagValueMap, List<CellData> resultList, int rowIndex, int columnIndex, String tag) {
        // 按照时间顺序从老到新排序
        List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < dayEach.size(); i++) {
            DateQuery query = dayEach.get(i);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();
            if (tag.endsWith("_evt")) {
                //如果是evt结尾的, 取时间最大的值
                Long key = Collections.max(clockList);
                Double maxVal = tagValueMap.get(key);
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
            } else {
                // 取结束时间点位、若没有值，取范围内第一个值
                Double val = tagValueMap.get(queryEndTime.getTime());
                if (Objects.nonNull(val)) {
                    ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                } else {
                    for (int j = 0; j < clockList.size(); j++) {
                        Long tempTime = clockList.get(j);
                        Date date = new Date(tempTime);
                        if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                            val = tagValueMap.get(tempTime);
                            ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                            break;
                        }
                    }
                }
            }
        }
    }



}
