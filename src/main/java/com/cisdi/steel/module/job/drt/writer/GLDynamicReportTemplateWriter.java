package com.cisdi.steel.module.job.drt.writer;

import com.alibaba.fastjson.JSON;
import com.cisdi.steel.common.poi.PoiCustomUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.dto.response.gl.TagValueMapDTO;
import com.cisdi.steel.dto.response.gl.req.TagQueryParam;
import com.cisdi.steel.module.job.drt.dto.HandleDataDTO;
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

    protected void handleData(HandleDataDTO handleDataDTO) {
        Workbook workbook = handleDataDTO.getWorkbook();
        WriterExcelDTO excelDTO = handleDataDTO.getExcelDTO();
        List<DateQuery> dateQuerys = handleDataDTO.getDateQuerys();
        String version = handleDataDTO.getVersion();
        HashMap<String, TargetManagement> targetManagementMap = handleDataDTO.getTargetManagementMap();
        Boolean needToWriteTime = handleDataDTO.getNeedToWriteTime();


        String queryUrl = getUrl(excelDTO.getTemplate().getSequence(), version);
        // 动态报表生成的模板默认取第二个sheet。
        Sheet mainSheet = workbook.getSheetAt(0);
        Sheet tagSheet = workbook.getSheetAt(1);

        // 直接拿到tag点名, 无需根据别名再去获取tag点名
        for (int rowNum = 0; rowNum < dateQuerys.size(); rowNum++) {
            List<CellData> cellDataList = handleEachRowData(targetManagementMap, queryUrl, dateQuerys.get(rowNum), rowNum + 1);
            ExcelWriterUtil.setCellValue(tagSheet, cellDataList);
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
    private List<CellData> handleEachRowData(HashMap<String, TargetManagement> targetManagementMap, String queryUrl, DateQuery dateQuery, int rowIndex) {
        Set<String> tagFormulasSet = targetManagementMap.keySet();
        // 拼接后的公式
        List<String> tagFormulas = new ArrayList<>(tagFormulasSet);
        // 批量查询tag value
        TagQueryParam tagQueryParam = new TagQueryParam(dateQuery.getQueryStartTime(), dateQuery.getQueryEndTime(), tagFormulas);
        String result = httpUtil.postJsonParams(queryUrl, JSON.toJSONString(tagQueryParam));

        List<CellData> resultList = new ArrayList<>();
        if (StringUtils.isNotBlank(result)) {
            TagValueMapDTO tagValueMapDTO = JSON.parseObject(result, TagValueMapDTO.class);
            Map<String, LinkedHashMap<Long, Double>> tagValueMaps = tagValueMapDTO.getData();
            // 获取targetManagement map
            for (int columnIndex = 0; columnIndex < tagFormulas.size(); columnIndex++) {
                String tagFormula = tagFormulas.get(columnIndex);
                if (StringUtils.isNotBlank(tagFormula)) {
                    TargetManagement targetManagement = targetManagementMap.get(tagFormula);
                    if (Objects.nonNull(targetManagement)) {
                        Map<Long, Double> tagValueMap = tagValueMaps.get(tagFormula);
                        if (Objects.nonNull(tagValueMap)) {
                            handleEachCellData(dateQuery, tagValueMap, resultList, rowIndex, columnIndex, tagFormula, targetManagement);
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
    private void handleEachCellData(DateQuery dateQuery, Map<Long, Double> tagValueMap, List<CellData> resultList,
                                    int rowIndex, int columnIndex, String tagFormula, TargetManagement tag) {
        // 按照时间顺序从老到新排序
        List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
        Date queryStartTime = dateQuery.getStartTime();
        Date queryEndTime = dateQuery.getEndTime();

        if (tagFormula.endsWith("_evt")) {
            //如果是evt结尾的, 取时间范围内最大值
            Double maxVal = tagValueMap.values().stream().max(Comparator.comparing(Double::doubleValue)).orElse(null);
            ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, maxVal);
        } else {
            // 其他情况，取时间范围内第一个值。
            for (int j = 0; j < clockList.size(); j++) {
                Long tempTime = clockList.get(j);
                Date date = new Date(tempTime);
                if (DateUtil.isDayBetweenAnotherTwoDays(date, queryStartTime, queryEndTime)) {
                    Double val = tagValueMap.get(tempTime);
                    ExcelWriterUtil.addCellData(resultList, rowIndex, columnIndex, val);
                    break;
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
