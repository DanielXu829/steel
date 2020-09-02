package com.cisdi.steel.report;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.mapper.ReportTemplateTagsMapper;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 单元测试 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
public class ReportTemplateTagsServiceTest extends SteelApplicationTests {

    @Autowired
    private ReportTemplateTagsService reportTemplateTagsService;

    @Autowired
    private ReportTemplateTagsMapper reportTemplateTagsMapper;

    @Test
    public void test_selectByConfigId(){
        List<ReportTemplateTags> reportTemplateTags = reportTemplateTagsService.selectBySheetId(1L);
        System.out.println(reportTemplateTags);
    }



}
