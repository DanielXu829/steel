package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.ReportTemplateTagsMapper;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    /**
     * 根据SheetId查询
     * @param sheetId
     * @return
     */
    public List<ReportTemplateTags> selectBySheetId(Long sheetId){
        LambdaQueryWrapper<ReportTemplateTags> wrapper = new QueryWrapper<ReportTemplateTags>().lambda();
        wrapper.eq(true, ReportTemplateTags::getTemplateSheetId, sheetId);
        wrapper.orderByAsc(ReportTemplateTags::getSequence);
        return reportTemplateTagsMapper.selectList(wrapper);
    }


    /**
     * 根据sheetId删除
     * @param sheetId
     * @return
     */
    public int deleteBySheetId(Long sheetId){
        LambdaQueryWrapper<ReportTemplateTags> wrapper = new QueryWrapper<ReportTemplateTags>().lambda();
        wrapper.eq(true, ReportTemplateTags::getTemplateSheetId, sheetId);
        return reportTemplateTagsMapper.delete(wrapper);
    }
    public ApiResult<List<TargetManagement>> test(Long sheetId) {

        List<TargetManagement> names = reportTemplateTagsMapper.test(sheetId);

        return ApiUtil.success(names);
    }
}
