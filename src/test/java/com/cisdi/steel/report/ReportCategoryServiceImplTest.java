package com.cisdi.steel.report;

import com.cisdi.steel.SteelApplicationTests;
import com.cisdi.steel.module.report.entity.ReportCategory;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.service.ReportCategoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ReportCategoryServiceImplTest extends SteelApplicationTests {
    @Autowired
    private ReportCategoryService reportCategoryService;

    @Test
    public void deleteCurrentTarget() {
        ReportCategory reportCategory = new ReportCategory();
        // reportCategory.setId(309l);
        reportCategoryService.deleteCurrentTarget(reportCategory);
    }

}