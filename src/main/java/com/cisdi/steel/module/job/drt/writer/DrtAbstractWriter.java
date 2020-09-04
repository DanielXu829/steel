package com.cisdi.steel.module.job.drt.writer;

import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategyContext;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.enums.TagCalSuffixEnum;
import com.cisdi.steel.module.report.enums.TagTimeSuffixEnum;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import org.springframework.beans.factory.annotation.Autowired;

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
}
