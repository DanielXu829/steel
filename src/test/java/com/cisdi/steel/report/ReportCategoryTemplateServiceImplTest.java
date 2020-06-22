package com.cisdi.steel.report;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ReportCategoryTemplateServiceImplTest extends SteelApplicationTests {
    @Autowired
    ReportCategoryTemplateService reportCategoryTemplateService;

    @Test
    public void deleteRecord() {
        BaseId baseId = new BaseId();
        // baseId.setId(394L);
        reportCategoryTemplateService.deleteRecord(baseId);
    }
}