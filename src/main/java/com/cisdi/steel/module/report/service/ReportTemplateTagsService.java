package com.cisdi.steel.module.report.service;

import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.common.base.service.IBaseService;

import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 服务类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
public interface ReportTemplateTagsService extends IBaseService<ReportTemplateTags> {

    public List<ReportTemplateTags> selectByConfigId(Long configId);

    /**
     * 根据模板配置id删除
     * @param configId
     * @return
     */
    public int deleteByConfigId(Long configId);
}
