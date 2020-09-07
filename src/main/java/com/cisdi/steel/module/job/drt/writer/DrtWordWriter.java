package com.cisdi.steel.module.job.drt.writer;

import cn.afterturn.easypoi.word.WordExportUtil;
import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.math.NumberArithmeticUtils;
import com.cisdi.steel.module.job.a1.doc.ChartFactory;
import com.cisdi.steel.module.job.a1.doc.Serie;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.drt.dto.DrtWriterDTO;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategy;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.dto.ReportTemplateSheetDTO;
import com.cisdi.steel.module.report.entity.*;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import com.cisdi.steel.module.report.enums.WordTypeEnum;
import com.cisdi.steel.module.report.service.ReportIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tools.ant.util.DateUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("ALL")
@Component
public class DrtWordWriter extends DrtAbstractWriter implements IDrtWriter<XWPFDocument> {

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportIndexService reportIndexService;

    public XWPFDocument drtWriter(DrtWriterDTO drtWriterDTO) {
        ReportCategoryTemplate currentTemplate = drtWriterDTO.getTemplate();
        Date recordDate = drtWriterDTO.getDateQuery().getRecordDate();
        ReportCategoryTemplate template = drtWriterDTO.getTemplate();
        // word结果集
        HashMap<String, Object> result = new HashMap<>();
        ReportTemplateConfigDTO reportTemplateConfigDTO =
                reportTemplateConfigService.getDTOById(template.getTemplateConfigId());
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigDTO.getReportTemplateConfig();
        String version = SequenceEnum.getVersion(reportTemplateConfig.getSequenceCode());
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = reportTemplateConfigDTO.getReportTemplateSheetDTOs();
        int sheetIndex = 1;
        for (ReportTemplateSheetDTO reportTemplateSheetDTO : reportTemplateSheetDTOs) {
            ReportTemplateSheet reportTemplateSheet = reportTemplateSheetDTO.getReportTemplateSheet();
            List<ReportTemplateTags> sheetTagList = reportTemplateSheetDTO.getReportTemplateTagsList();
            sheetTagList.sort(Comparator.comparing(ReportTemplateTags::getSequence));
            List<Long> tagIdList = sheetTagList.stream().map(ReportTemplateTags::getTargetId).collect(Collectors.toList());
            List<TargetManagement> targetManagements = targetManagementMapper.listByIds(tagIdList);
            List<String> oldTagFormulas = targetManagements.stream().map(TargetManagement::getTargetFormula)
                    .collect(Collectors.toList());
            List<String> newTagFormulas = joinSuffix(oldTagFormulas, sheetTagList); // 拼接tag点前缀和后缀
            Map<String, TargetManagement> tagFormulaToTargetMap = newTagFormulas.stream().collect(
                    Collectors.toMap(key -> key, key -> targetManagements.get(newTagFormulas.indexOf(key)),
                            (key1, key2) -> key2, LinkedHashMap::new));
            SequenceEnum sequenceEnum = SequenceEnum.getSequenceEnumByCode(template.getSequence());
            HandleQueryDataStrategy handleStrategy =
                    handleQueryDataStrategyContext.getHandleQueryDataStrategy(sequenceEnum.getSequenceCode());
            List<DateQuery> dateQuerys = handleStrategy.getDateQueries(recordDate, reportTemplateSheet); // 获取查询策略
            WordTypeEnum wordTypeEnum = WordTypeEnum.getByCode(reportTemplateSheet.getWordType());
            switch (wordTypeEnum) {
                case PLAIN_TEXT:
                    handlePlainText(result, version, sheetIndex, reportTemplateSheet, handleStrategy, dateQuerys, tagFormulaToTargetMap);
                    break;
                default:
                    handleChart(result, version, sheetIndex, reportTemplateSheet, handleStrategy, dateQuerys, tagFormulaToTargetMap);
                    break;
            }
            sheetIndex++;
        }

        Date date = DateUtil.addDays(recordDate, -1);
        result.put("current_date", DateUtils.format(date, DateUtil.yyyyMMddChineseFormat));

        try {
            XWPFDocument document = WordExportUtil.exportWord07(currentTemplate.getTemplatePath(), result);
            return document;
        } catch (Exception e) {
            throw new RuntimeException("word文档生成失败");
        }
    }

    /**
     * 处理纯文本
     * @param result
     * @param version
     * @param sheetIndex
     * @param reportTemplateSheet
     * @param handleStrategy
     * @param dateQueries
     * @param tagFormulaToTargetMap
     */
    private void handlePlainText(HashMap<String, Object> result, String version, int sheetIndex, ReportTemplateSheet reportTemplateSheet,
                             HandleQueryDataStrategy handleStrategy, List<DateQuery> dateQueries,
                             Map<String, TargetManagement> tagFormulaToTargetMap) {
        try {
            List<String> tagFormulas = new ArrayList<>(tagFormulaToTargetMap.keySet());
            Map<String, List<Double>> tagNameToValueListMap = new HashMap<>();
            for (String tagFormula : tagFormulas) {
                tagNameToValueListMap.put(tagFormula, new ArrayList<Double>());
            }
            for (DateQuery dateQuery : dateQueries) {
                // 调api批量查询tag点数据
                Map<String, LinkedHashMap<Long, Double>> tagValueMaps
                        = handleStrategy.getTagValueMaps(dateQuery, version, tagFormulas);
                Map<String, Double> tagNameToValueMap = getTagFormulaToValueMap(tagValueMaps, dateQuery, tagFormulas);
                for (String tagFormula : tagFormulas) {
                    tagNameToValueListMap.get(tagFormula).add(tagNameToValueMap.get(tagFormula));
                }
            }
            int tagIndex = 1;
            int tagFormulasSize = tagFormulas.size();
            for (String tagFormula : tagFormulas) {
                Double lastValue = tagNameToValueListMap.get(tagFormula).get(tagFormulasSize - 1);
                Double firstValue = tagNameToValueListMap.get(tagFormula).get(0);
                Double difference = lastValue - firstValue;
                String compare = difference > 0d ? "升高" : "降低";
                difference = Math.abs(difference);
                result.put(String.format("sheet%s_tag%s", sheetIndex, tagIndex), parse(lastValue));
                result.put(String.format("sheet%s_difference%s", sheetIndex, tagIndex), parse(difference));
                result.put(String.format("sheet%s_compare%s", sheetIndex, tagIndex), compare);
                tagIndex++;
            }
        } catch (Exception e) {
            log.error("处理纯文本部分出错", e);
        }
    }
    /**
     * 生成折线图
     * @param version
     * @param sheetIndex
     * @param reportTemplateSheet
     * @param handleStrategy
     * @param dateQueries
     * @param tagFormulaToTargetMap
     */
    private void handleChart(HashMap<String, Object> result, String version, int sheetIndex, ReportTemplateSheet reportTemplateSheet,
                             HandleQueryDataStrategy handleStrategy, List<DateQuery> dateQueries,
                             Map<String, TargetManagement> tagFormulaToTargetMap) {
        try {
            // 横坐标
            List<String> dateStrList = getDateStrList(reportTemplateSheet, dateQueries);
            // 点名和数据集合的map
            List<String> tagFormulas = new ArrayList<>(tagFormulaToTargetMap.keySet());
            Map<String, List<Double>> tagNameToValueListMap = new HashMap<>();
            for (String tagFormula : tagFormulas) {
                tagNameToValueListMap.put(tagFormula, new ArrayList<Double>());
            }
            for (DateQuery dateQuery : dateQueries) {
                // 调api批量查询tag点数据
                Map<String, LinkedHashMap<Long, Double>> tagValueMaps
                        = handleStrategy.getTagValueMaps(dateQuery, version, tagFormulas);
                Map<String, Double> tagNameToValueMap = getTagFormulaToValueMap(tagValueMaps, dateQuery, tagFormulas);
                for (String tagFormula : tagFormulas) {
                    tagNameToValueListMap.get(tagFormula).add(tagNameToValueMap.get(tagFormula));
                }
            }

            // tag点分组，一组两个点，两个点对应一个chart
            List<List<String>> tagListGroups = ListUtils.partition(tagFormulas, 2);
            int chartIndex = 1;
            for (List<String> tagListGroup : tagListGroups) {
                List<Vector<Serie>> vectors = new ArrayList<>();
                List<String> yLableList = new ArrayList<>();
                double min1 = 0, max1 = 100, min2 = 0, max2 = 100;
                int tagListSize = tagListGroup.size();
                for (int i = 0; i < tagListSize; i++) {
                    String tagFormula = tagListGroup.get(i);
                    Vector<Serie> series = new Vector<>();
                    TargetManagement targetManagement = tagFormulaToTargetMap.get(tagFormula);
                    String writtenName = targetManagement.getWrittenName();
                    List<Double> values = tagNameToValueListMap.get(tagFormula);
                    if (i == 0) {
                        min1 = getAxisMinValue(values);
                        max1 = getAxisMaxValue(values);
                    } else {
                        min2 = getAxisMinValue(values);
                        max2 = getAxisMaxValue(values);
                    }
                    series.add(new Serie(writtenName, values.toArray()));
                    vectors.add(series);
                    yLableList.add(writtenName);
                }
                String title1 = "";
                String categoryAxisLabel1 = "";
                String[] yLabels = yLableList.stream().toArray(String[]::new);

                int[] stack = {1, 1};
                int[] ystack = {1, 2};

                JFreeChart Chart;
                // y轴只有两条或者1条
                if (tagListSize == 2) {
                    Chart = ChartFactory.createLineChart(title1,
                            categoryAxisLabel1, yLabels, vectors,
                            dateStrList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, min2, max2, 0, 0, 2, stack, ystack);
                } else {
                    Chart = ChartFactory.createLineChart(title1,
                            categoryAxisLabel1, yLabels, vectors,
                            dateStrList.toArray(), CategoryLabelPositions.UP_45, true, min1, max1, 0, 0, 0, 0, 1, stack, ystack);
                }

                WordImageEntity image1 = image(Chart);
                result.put(String.format("sheet%s_chart%s", sheetIndex, chartIndex), image1);
                chartIndex++;
            }
        } catch (Exception e) {
            log.error("处理JfreeChart部分出错", e);
        }
    }

    /**
     * 获取map: key为tagformula，value为当前dateQuery下的value
     * @param tagValueMaps
     * @param dateQuery
     * @param tagFormulas
     * @return
     */
    private Map<String, Double> getTagFormulaToValueMap(Map<String, LinkedHashMap<Long, Double>> tagValueMaps, DateQuery dateQuery,
                              List<String> tagFormulas) {
        Map<String, Double> tagFormulaToValueMap = new HashMap<>();
        for (String tagFormula : tagFormulas) {
            Map<Long, Double> tagValueMap = tagValueMaps.get(tagFormula);
            if (Objects.isNull(tagValueMap)) {
                continue;
            }
            Double value = 0d; //默认值为0d
            // 按照时间顺序从老到新排序
            List<Long> clockList = tagValueMap.keySet().stream().sorted().collect(Collectors.toList());
            Date queryStartTime = dateQuery.getStartTime();
            Date queryEndTime = dateQuery.getEndTime();
            if (tagFormula.endsWith("_evt")) {
                //如果是evt结尾的, 取时间范围内最大值
                value = tagValueMap.values().stream().max(Comparator.comparing(Double::doubleValue)).orElse(value);
            } else {
                // 默认取开始时间点
                if (clockList.contains(queryStartTime.getTime())) {
                    value = tagValueMap.get(queryStartTime.getTime());
                } else {
                    // 取第一个值
                    for (int j = 0; j < clockList.size(); j++) {
                        Long tempTime = clockList.get(j);
                        Date date = new Date(tempTime);
                        if (DateUtil.isDayBetweenAnotherTwoDays(date, queryStartTime, queryEndTime)) {
                            value = tagValueMap.get(tempTime);
                            break;
                        }
                    }
                }
            }
            tagFormulaToValueMap.put(tagFormula, value);
        }
        return tagFormulaToValueMap;
    }

    // 处理double数据 四舍五入保留2位小数
    private String parse(Double v) {
        if (v == null) {
            return "   ";
        }
        Double value = NumberArithmeticUtils.roundingX(v, 2);
        if (value.equals(0d)) {
            return "0";
        } else {
            return value.toString();
        }
    }

}
