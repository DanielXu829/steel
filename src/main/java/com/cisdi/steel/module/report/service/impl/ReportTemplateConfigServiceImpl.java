package com.cisdi.steel.module.report.service.impl;

import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.job.config.JobProperties;
import com.cisdi.steel.module.report.dto.ReportTemplateConfigDTO;
import com.cisdi.steel.module.report.entity.ReportTemplateConfig;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.ReportTemplateConfigMapper;
import com.cisdi.steel.module.report.service.ReportTemplateConfigService;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import com.cisdi.steel.module.report.service.TargetManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.dsig.Manifest;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Description: 报表动态模板配置 服务实现类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Service
@Slf4j
public class ReportTemplateConfigServiceImpl extends BaseServiceImpl<ReportTemplateConfigMapper, ReportTemplateConfig> implements ReportTemplateConfigService {

    @Autowired
    private JobProperties jobProperties;

    @Autowired
    private ReportTemplateTagsService reportTemplateTagsService;

    @Autowired
    private TargetManagementService targetManagementService;

    @Autowired
    private ReportTemplateConfigMapper reportTemplateConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateDTO(ReportTemplateConfigDTO templateConfigDTO) {
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        if (reportTemplateConfig.getId() != null && reportTemplateConfig.getId() > 0){
            this.updateRecord(reportTemplateConfig);
        } else {
            this.insertRecord(reportTemplateConfig);
        }

        long configId = reportTemplateConfig.getId();
        List<ReportTemplateTags> reportTemplateTags = templateConfigDTO.getReportTemplateTags();
        //清空参数列表后再插入
        reportTemplateTagsService.deleteByConfigId(configId);
        reportTemplateTags.stream().forEach(tag -> {
            tag.setTemplateConfigId(configId);
            reportTemplateTagsService.insertRecord(tag);
        });

        return true;
    }

    @Override
    public ReportTemplateConfigDTO getDTOById(Long id) {
        ReportTemplateConfig reportTemplateConfig = reportTemplateConfigMapper.selectById(id);
        if (reportTemplateConfig != null) {
            ReportTemplateConfigDTO configDTO = new ReportTemplateConfigDTO();
            configDTO.setReportTemplateConfig(reportTemplateConfig);

            //查询tags
            List<ReportTemplateTags> reportTemplateTags = reportTemplateTagsService.selectByConfigId(reportTemplateConfig.getId());
            configDTO.setReportTemplateTags(reportTemplateTags);

            return configDTO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResult deleteRecord(BaseId record) {
        reportTemplateTagsService.deleteByConfigId(record.getId());
        reportTemplateConfigMapper.deleteById(record.getId());

        return ApiUtil.success("删除成功");
    }

    public String generateTemplate(ReportTemplateConfigDTO templateConfigDTO) {
        //通过templateConfig获取所有配置项。
        ReportTemplateConfig reportTemplateConfig = templateConfigDTO.getReportTemplateConfig();
        List<ReportTemplateTags> reportTemplateTags = reportTemplateTagsService.selectByConfigId(reportTemplateConfig.getId());

        if (CollectionUtils.isNotEmpty(reportTemplateTags)) {
            List<Long> targetIds = reportTemplateTags.stream().map(ReportTemplateTags::getTargetId).collect(Collectors.toList());
            //通过配置获取所有tag management.
            Collection<TargetManagement> targetManagements = targetManagementService.listByIds(targetIds);

            log.info("targetManagement：" + targetManagements.toString());
        }

        return null;
    }

    private void generateReportTemplateExcel(ReportTemplateConfig reportTemplateConfig, List<ReportTemplateTags> reportTemplateTags, List<TargetManagement> targetManagements){
        String tempPath = jobProperties.getTempPath();
    }

}
