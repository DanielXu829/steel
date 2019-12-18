package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.common.base.service.IBaseService;

/**
 * <p>Description: 报表动态模板配置 服务类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
public interface ReportTemplateConfigService extends IBaseService<ReportTemplateConfig> {

    /**
     * 添加或更新整个报表模板配置，包含参数列表
     * @param templateConfigDTO
     * @return
     */
    public boolean saveOrUpdateDTO(ReportTemplateConfigDTO templateConfigDTO);

    /**
     * 查询整个报表模板配置，包含参数列表
     * @param id
     * @return
     */
    public ReportTemplateConfigDTO getDTOById(Long id);

    /**
     * 生成模板
     * @param reportTemplateConfigDTO
     * @return 临时文件生成路径
     */
    public String generateTemplate(ReportTemplateConfigDTO reportTemplateConfigDTO);


}
