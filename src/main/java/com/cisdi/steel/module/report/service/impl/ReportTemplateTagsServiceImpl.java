package com.cisdi.steel.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cisdi.steel.common.base.service.impl.BaseServiceImpl;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.module.report.entity.SysConfig;
import com.cisdi.steel.module.report.entity.TagsName;
import com.cisdi.steel.module.report.entity.TargetManagement;
import com.cisdi.steel.module.report.mapper.ReportTemplateTagsMapper;
import com.cisdi.steel.module.report.service.ReportTemplateTagsService;
import io.swagger.annotations.Api;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.service.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /** 通过sheetId查询出表对应的点 */
    public List<String> selectTagNameBySheetId(String sheetId) {
        List<String> names = reportTemplateTagsMapper.selectTagNameBySheetId(sheetId);
        return names;
    }
    /** 通过sheetId查询出对应点的字段*/
    public List<String> test1(String sheetId) {
        return reportTemplateTagsMapper.test1(sheetId);
    }

    public ApiResult<List<String>> selectTagNameByCode(String code) {
        List<String> names = reportTemplateTagsMapper.selectTagNameByCode(code);
        return null;
    }
    public String selectUrlByCode(String code) {
        SysConfig s1 = reportTemplateTagsMapper.selectUrlByCode(code);
        String url = s1.getAction() + s1.getCode() + s1.getUrl();
        System.out.println(url);
        return url;
    }

    @Override
    public List<String> tagName(String id,String code) {
        List<TagsName> t1s  =  reportTemplateTagsMapper.tagName(id,code);
        List<String> str = new ArrayList<>();
        String s = "";
        if (t1s != null && t1s.size() > 0) {
            for (TagsName t1 : t1s) {
                s = code.toUpperCase() + "_" + t1.getTargetName() + "_" + t1.getTagTimeSuffix() + "_" + t1.getTagCalSuffix();
                str.add(s);
            }
        }
        // String s = "BF2" + "_" + t1.getTargetName() + "_" + t1.getTagTimeSuffix() + "_" +t1.getTagCalSuffix();

        return str;
    }
}
