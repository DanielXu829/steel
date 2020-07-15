package com.cisdi.steel.module.job.drt.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.module.job.dto.CellData;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.ExcelWriterUtil;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.job.util.date.DateQueryUtil;
import com.cisdi.steel.module.report.entity.TargetManagement;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Description: 高炉动态报表 执行处理类 </p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <P>Date: 2020/1/7 </P>
 *
 * @version 1.0
 */
@Component
@SuppressWarnings("ALL")
@Slf4j
public class GLDynamicReportTemplateWriter extends DynamicReportTemplateWriter {

    protected void handleData(WriterExcelDTO excelDTO, Workbook workbook, String version) {
        String queryUrl = getUrl(excelDTO.getTemplate().getSequence(), version);

        // 动态报表生成的模板默认取第二个sheet。
        Sheet sheet = workbook.getSheetAt(1);
        String sheetName = sheet.getSheetName();
        String[] sheetSplit = sheetName.split("_");
        if (sheetSplit.length == 4) {
            DateQuery dateQuery = this.getDateQuery(excelDTO);
            List<DateQuery> dateQueries = this.getHandlerData(sheetSplit, dateQuery.getRecordDate());
            // evt为后缀的值需要此逻辑，防止取到前一天累计的值。
            DateQuery firstDateQuery = dateQueries.get(0);
            firstDateQuery.setStartTime(DateUtil.getDateBeginTime(firstDateQuery.getEndTime()));
            dateQueries.set(0, firstDateQuery);

            // 直接拿到tag点名, 无需根据别名再去获取tag点名
            List<String> tagNames = PoiCustomUtil.getFirstRowCelVal(sheet);
            for (int rowNum = 0; rowNum < dateQueries.size(); rowNum++) {
                List<CellData> cellDataList = handleEachRowData(tagNames, queryUrl, dateQueries.get(rowNum), rowNum + 1);
                ExcelWriterUtil.setCellValue(sheet, cellDataList);
            }
        }
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
        List<TargetManagement> targetManagements = targetManagementMapper.selectTargetManagementsByTargetNames(tagNames);
        Map<String, TargetManagement> targetManagementMap = targetManagements.stream().collect(Collectors.toMap(TargetManagement::getTargetName, target -> target));
        List<String> tagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula).collect(Collectors.toList());
        // 批量查询tag value
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        String result = httpUtil.postJsonParams(queryUrl, JSON.toJSONString(tagQueryParam));

        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
            Map<String, LinkedHashMap<Long, Double>> tagValueMaps = tagValueMapDTO.getData();
            // 获取targetManagement map

            for (int columnIndex = 0; columnIndex < tagNames.size(); columnIndex++) {
                String targetName = tagNames.get(columnIndex);
                if (StringUtils.isNotBlank(targetName)) {
                    TargetManagement targetManagement = targetManagementMap.get(targetName);
                    if (Objects.nonNull(targetManagement)) {
                        Map<Long, Double> tagValueMap = tagValueMaps.get(targetManagement.getTargetFormula());
                        if (Objects.nonNull(tagValueMap)) {
                            TargetManagement tag = targetManagementMap.get(targetName);
                            List<DateQuery> dayEach = DateQueryUtil.buildDayHourOneEach(new Date(dateQuery.getQueryStartTime()), new Date(dateQuery.getQueryEndTime()));
                            handleEachCellData(dayEach, tagValueMap, resultList, rowIndex, columnIndex, tag);
                        }
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
    private void handleEachCellData(List<DateQuery> dayEach, Map<Long, Double> tagValueMap, List<CellData> resultList, int rowIndex, int columnIndex, TargetManagement tag) {
        // 按照时间顺序从老到新排序
        List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
        for (int i = 0; i < dayEach.size(); i++) {
            DateQuery query = dayEach.get(i);
            Date queryStartTime = query.getStartTime();
            Date queryEndTime = query.getEndTime();

            if (tag.getTargetFormula().endsWith("_evt")) {
                //如果是evt结尾的, 取时间范围内最大值
                Double maxVal = 0.0d;
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double defaultVal = tagValueMap.get(tempTime);
                        if (defaultVal > maxVal) {
                            maxVal = defaultVal;
                        }
                    }
                }
                ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
            } else {
                // 其他情况，取时间范围内第一个值。
                for (int j = 0; j < clockList.size(); j++) {
                    Long tempTime = clockList.get(j);
                    Date date = new Date(tempTime);
                    if ((date.getTime() >= queryStartTime.getTime()) && (date.getTime() <= queryEndTime.getTime())) {
                        Double val = tagValueMap.get(tempTime);
                        ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 通过tag点拿数据的API，根据sequence和version返回不同工序的api地址
     *
     * @param sequence
     * @param version
     * @return
     */
    protected String getUrl(String sequence, String version) {
        return httpProperties.getGlUrlVersion(version) + "/getTagValues/tagNamesInRange";
    }

}
