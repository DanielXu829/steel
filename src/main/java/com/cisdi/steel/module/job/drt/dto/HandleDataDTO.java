package com.cisdi.steel.module.job.drt.dto;

import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.enums.SequenceEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.List;

@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class HandleDataDTO {
    private WriterExcelDTO excelDTO;
    private Workbook workbook;
    private String version;
    private HashMap<String, TargetManagement> targetManagementMap;
    private List<DateQuery> dateQuerys;
    private ReportTemplateConfig reportTemplateConfig;
    private SequenceEnum sequenceEnum;
}
