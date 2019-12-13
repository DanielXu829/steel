package com.cisdi.steel.report;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * <p>Description: 报表动态模板配置 单元测试 </p>
 * <P>Date: 2019-12-12 </P>
 *
 * @author cisdi
 * @version 1.0
 */
public class ReportTemplateConfigServiceTest extends SteelApplicationTests {

    @Autowired
    private ReportTemplateConfigService reportTemplateConfigService;

    @Test
    public void test_generateTemplate(){
        ReportTemplateConfig templateConfig = reportTemplateConfigService.getById(316);

    }

    @Test
    public void test_saveOrUpdate(){
        ReportTemplateConfigDTO configDTO = new ReportTemplateConfigDTO();

        ReportTemplateConfig templateConfig = new ReportTemplateConfig();
        templateConfig.setId(318L);
        templateConfig.setTemplateName("测试2019-12-12 11:42:38");
        configDTO.setReportTemplateConfig(templateConfig);


        ReportTemplateTags tags1 = new ReportTemplateTags();
        tags1.setTargetId(153L);
        tags1.setSequence(1);

        ReportTemplateTags tags2 = new ReportTemplateTags();
        tags2.setTargetId(154L);
        tags2.setSequence(2);
        ArrayList<ReportTemplateTags> reportTemplateTags = Lists.newArrayList(tags1, tags2);
        configDTO.setReportTemplateTags(reportTemplateTags);

        reportTemplateConfigService.saveOrUpdateDTO(configDTO);
    }

    @Test
    public void test_getDTOById(){

        ReportTemplateConfigDTO dtoById = reportTemplateConfigService.getDTOById(318L);
        System.out.println(dtoById);
    }
}
