package com.cisdi.steel.module.report.dto;

import com.cisdi.steel.module.report.entity.ReportTemplateSheet;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReportTemplateSheetDTO {
    @NotNull
    private ReportTemplateSheet reportTemplateSheet;
    @NotEmpty
    private List<ReportTemplateTags> reportTemplateTagsList;
}
