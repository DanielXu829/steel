package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.mapper.ReportTemplateTagsMapper;
import com.cisdi.steel.module.report.mapper.TargetManagementMapper;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 服务实现类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
@Service
public class ReportTemplateTagsServiceImpl extends BaseServiceImpl<ReportTemplateTagsMapper, ReportTemplateTags> implements ReportTemplateTagsService {

    @Autowired
    private ReportTemplateTagsMapper reportTemplateTagsMapper;

    @Autowired
    private TargetManagementMapper targetManagementMapper;

    /**
     * 根据模板配置查询
     * @param configId
     * @return
     */
    public List<ReportTemplateTags> selectByConfigId(Long configId){

        LambdaQueryWrapper<ReportTemplateTags> wrapper = new QueryWrapper<ReportTemplateTags>().lambda();
        wrapper.eq(true, ReportTemplateTags::getTemplateConfigId, configId);
        wrapper.orderByAsc(ReportTemplateTags::getSequence);
        List<ReportTemplateTags> reportTemplateTags = reportTemplateTagsMapper.selectList(wrapper);

        return reportTemplateTags;
    }


    /**
     * 根据模板配置id删除
     * @param configId
     * @return
     */
    public int deleteByConfigId(Long configId){

        LambdaQueryWrapper<ReportTemplateTags> wrapper = new QueryWrapper<ReportTemplateTags>().lambda();
        wrapper.eq(true, ReportTemplateTags::getTemplateConfigId, configId);
        return reportTemplateTagsMapper.delete(wrapper);

    }

}
