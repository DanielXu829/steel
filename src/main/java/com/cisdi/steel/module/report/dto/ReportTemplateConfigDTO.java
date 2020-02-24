package com.cisdi.steel.module.report.dto;

import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import lombok.Data;

import java.util.List;

/**
 * 动态报表配置 dto
 */
@Data
public class ReportTemplateConfigDTO {
    private ReportTemplateConfig reportTemplateConfig;
    private List<ReportTemplateTags> reportTemplateTags;
}
