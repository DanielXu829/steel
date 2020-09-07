package com.cisdi.steel.module.job.drt.dto;

import com.cisdi.steel.module.job.drt.writer.strategy.query.HandleQueryDataStrategy;
import com.cisdi.steel.module.job.dto.WriterExcelDTO;
import com.cisdi.steel.module.job.util.date.DateQuery;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateSheet;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class HandleDataDTO {
    private DrtWriterDTO drtWriterDTO;
    private Workbook workbook;
    private String version;
    private List<String> newTagFormulas;
    private List<DateQuery> dateQuerys;
    private ReportTemplateSheet reportTemplateSheet;
    private HandleQueryDataStrategy handleStrategy;
}
