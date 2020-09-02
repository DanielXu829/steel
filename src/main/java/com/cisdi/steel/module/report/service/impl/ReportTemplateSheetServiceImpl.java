package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.module.report.entity.ReportTemplateSheet;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.mapper.ReportTemplateSheetMapper;
import com.cisdi.steel.module.report.mapper.ReportTemplateTagsMapper;
import com.cisdi.steel.module.report.service.ReportTemplateSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportTemplateSheetServiceImpl extends BaseServiceImpl<ReportTemplateSheetMapper
        , ReportTemplateSheet> implements ReportTemplateSheetService {

    @Autowired
    private ReportTemplateSheetMapper reportTemplateSheetMapper;

    /**
     * 通过配置id获取sheet
     * @param configId
     * @return
     */
    public List<ReportTemplateSheet> selectByConfigId(Long configId){
        LambdaQueryWrapper<ReportTemplateSheet> wrapper
                = new QueryWrapper<ReportTemplateSheet>().lambda();
        wrapper.eq(ReportTemplateSheet::getTemplateConfigId, configId);
        wrapper.orderByAsc(ReportTemplateSheet::getSequence);
        return reportTemplateSheetMapper.selectList(wrapper);
    }

    /**
     * 根据配置id删除对应的sheet
     * @param configId
     * @return
     */
    public int deleteByConfigId(Long configId){
        LambdaQueryWrapper<ReportTemplateSheet> wrapper
                = new QueryWrapper<ReportTemplateSheet>().lambda();
        wrapper.eq(ReportTemplateSheet::getTemplateConfigId, configId);
        return reportTemplateSheetMapper.delete(wrapper);
    }
}
