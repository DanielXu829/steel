package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.entity.ReportTemplateTags;
import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.module.report.entity.TagsName;
import com.cisdi.steel.module.report.entity.TargetManagement;

import java.util.List;

/**
 * <p>Description: 报表动态模板 - 参数列表 服务类 </p>
 * <P>Date: 2019-12-11 </P>
 *
 * @author cisdi
 * @version 1.0
 */
public interface ReportTemplateTagsService extends IBaseService<ReportTemplateTags> {

    public List<ReportTemplateTags> selectBySheetId(Long configId);

    /**
     * 根据模板配置id删除
     * @param configId
     * @return
     */
    public int deleteBySheetId(Long configId);

    List<String> selectTagNameBySheetId(String sheetId);
    List<String> test1(String sheetId);
    ApiResult<List<String>> selectTagNameByCode(String code);

    String selectUrlByCode(String code);

    List<String> tagName(String id,String code);
}
