package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.module.report.entity.ReportTemplateSheet;

import java.util.List;

public interface ReportTemplateSheetService extends IBaseService<ReportTemplateSheet> {

    List<ReportTemplateSheet> selectByConfigId(Long configId);

    int deleteByConfigId(Long configId);
}
