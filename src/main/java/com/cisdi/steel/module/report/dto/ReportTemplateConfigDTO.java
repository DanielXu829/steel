package com.cisdi.steel.module.report.dto;

import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateSheet;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 动态报表配置 dto
 */
@Data
public class ReportTemplateConfigDTO {
    @NotNull(message = "模板配置信息不能为空")
    @Valid
    private ReportTemplateConfig reportTemplateConfig;
    @NotEmpty(message = "模板sheet列表不能为空")
    private List<ReportTemplateSheetDTO> reportTemplateSheetDTOs;
}
