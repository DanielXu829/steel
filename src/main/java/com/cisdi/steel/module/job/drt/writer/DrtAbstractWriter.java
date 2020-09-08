package com.cisdi.steel.module.job.drt.writer;

import cn.afterturn.easypoi.word.entity.WordImageEntity;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategyContext;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportTemplateSheet;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.enums.TagCalSuffixEnum;
import com.cisdi.steel.module.report.enums.TagTimeSuffixEnum;
import com.cisdi.steel.module.report.enums.TimeDivideEnum;
import com.cisdi.steel.module.report.enums.TimeTypeEnum;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import com.cisdi.steel.module.report.service.TargetManagementService;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DrtAbstractWriter {

    @Autowired
    protected TargetManagementMapper targetManagementMapper;

    @Autowired
    protected ReportTemplateConfigService reportTemplateConfigService;

    @Autowired
    protected HandleQueryDataStrategyContext handleQueryDataStrategyContext;

    @Autowired
    protected TargetManagementService targetManagementService;

    /**
     * 拼接tagformula前缀和后缀
     * @param tagNames
     * @param reportTemplateTags
     * @return
     */
    protected List<String> joinSuffix(List<String> tagNames, List<ReportTemplateTags> reportTemplateTags) {
        List<String> tagTimeSuffixCodeList = TagTimeSuffixEnum.getTagTimeSuffixCodeList();
        List<String> tagCalSuffixCodeList = TagCalSuffixEnum.getTagCalSuffixCodeList();
        List<String> newTagFormulas = new ArrayList<>();
        // 截取最后两位原始后缀 拼接配置的后缀
        for (int i = 0; i < tagNames.size(); i++) {
            String tagName = tagNames.get(i);
            ReportTemplateTags reportTemplateTag = reportTemplateTags.get(i);
            for (String calCode : tagCalSuffixCodeList) {
                if (StringUtils.endsWith(tagName, "_" + calCode)) {
                    tagName = tagName.substring(0, tagName.length() - calCode.length() - 1);
                    break;
                }
            }
            for (String timeCode : tagTimeSuffixCodeList) {
                if (StringUtils.endsWith(tagName, "_" + timeCode)) {
                    tagName = tagName.substring(0, tagName.length() - timeCode.length() - 1);
                    break;
                }
            }
            List<String> stringsNeedToJoin = Arrays.asList(tagName, reportTemplateTag.getTagTimeSuffix(),
                    reportTemplateTag.getTagCalSuffix());
            newTagFormulas.add(String.join("_", stringsNeedToJoin));
        }
        return newTagFormulas;
    }

    /**
     * 获取日期字符串列表(word chart的横坐标)
     * @param reportTemplateSheet
     * @param dateQueries
     * @return
     */
    protected List<String> getDateStrList(ReportTemplateSheet reportTemplateSheet, List<DateQuery> dateQueries) {
        String timeType = reportTemplateSheet.getTimeType();
        TimeTypeEnum timeTypeEnum = TimeTypeEnum.getEnumByCode(timeType);
        Integer timeDivideType = reportTemplateSheet.getTimeDivideType();
        TimeDivideEnum timeDivideEnum = TimeDivideEnum.getEnumByCode(timeDivideType);
        List<String> dateStrList = new ArrayList<>();
        for (int i = 0; i < dateQueries.size(); i++) {
            DateQuery dateQuery = dateQueries.get(i);
            SimpleDateFormat format;
            String dateString = "";
            if (TimeTypeEnum.TIME_RANGE.equals(timeTypeEnum)) {
                // 每个月的月底日期不确定，特殊处理
                switch (timeDivideEnum) {
                    case HOUR:
                        format = new SimpleDateFormat(DateUtil.hhmmFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case DAY:
                        format = new SimpleDateFormat(DateUtil.ddChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case MONTH:
                        format = new SimpleDateFormat(DateUtil.MMChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                }
            } else {
                switch (timeDivideEnum) {
                    case HOUR:
                        format = new SimpleDateFormat(DateUtil.yyyyMMddHHChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case DAY:
                        format = new SimpleDateFormat(DateUtil.yyyyMMddChineseFormat);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                    case MONTH:
                        format = new SimpleDateFormat(DateUtil.yyyyMM);
                        dateString = format.format(dateQuery.getStartTime());
                        break;
                }
            }
            dateStrList.add(dateString);
        }
        return dateStrList;
    }

    /**
     * 获取坐标轴最大值
     * @param valueList
     * @return
     */
    protected Double getAxisMaxValue(List<Double> valueList) {
        Double max = valueList.stream().mapToDouble(e -> e).max().orElse(100d);
        if (max < 0) {
            return max * 0.8;
        }
        return max * 1.2;
    }

    /**
     * 获取坐标轴最小值
     * @param valueList
     * @return
     */
    protected Double getAxisMinValue(List<Double> valueList) {
        Double min = valueList.stream().mapToDouble(e -> e).min().orElse(0d);
        if (min < 0) {
            return min * 1.2;
        }
        return min * 0.8;
    }

    protected WordImageEntity image(JFreeChart chart) {
        WordImageEntity image = new WordImageEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            chart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            chart.getPlot().setBackgroundAlpha(0.1f);
            chart.getPlot().setNoDataMessage("当前没有有效的数据");
            ChartUtilities.writeChartAsJPEG(baos, chart, 600, 290);
            image.setHeight(256);
            image.setWidth(554);
            image.setData(baos.toByteArray());
            image.setType(WordImageEntity.Data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return image;
    }
}
