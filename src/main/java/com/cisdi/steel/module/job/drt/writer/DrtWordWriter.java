package com.cisdi.steel.module.job.drt.writer;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.job.drt.dto.DrtWriterDTO;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.dto.ReportTemplateSheetDTO;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.service.ReportIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

        ReportTemplateConfigDTO reportTemplateConfigDTO =
                reportTemplateConfigService.getDTOById(template.getTemplateConfigId());
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigDTO.getReportTemplateConfig();
        List<ReportTemplateSheetDTO> reportTemplateSheetDTOs = reportTemplateConfigDTO.getReportTemplateSheetDTOs();


        HashMap<String, Object> result = new HashMap<>();
        Date date = DateUtil.addDays(new Date(), -1);
        result.put("current_date", DateUtils.format(date, DateUtil.yyyyMMddChineseFormat));
        try {
            XWPFDocument document = WordExportUtil.exportWord07(currentTemplate.getTemplatePath(), result);
            return document;
        } catch (Exception e) {
            throw new RuntimeException("word文档生成失败");
        }
    }
}
