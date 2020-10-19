package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.query.ReportCategoryTemplateQuery;

import java.util.List;

/**
 * <p>Description: 分类模板配置 服务类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface ReportCategoryTemplateService extends IBaseService<ReportCategoryTemplate> {

    /**
     * 分页列表
     *
     * @param query 查询条件
     * @return 结果
     */
    ApiResult pageList(ReportCategoryTemplateQuery query);

    ApiResult getById(Long id);

    /**
     * 通过编码查询模板信息
     *
     * @param code 编码
     * @param lang 所属语言
     * @return 结果
     */
    List<ReportCategoryTemplate> selectTemplateInfo(String code, String lang, String sequence);

    /**
     * 插入数据
     *
     * @param record 保存的数据
     * @return 返回结果
     */
    ApiResult insertRecord(ReportCategoryTemplate record);

    /**
     * 更新数据
     *
     * @param record 保存的数据
     * @return 返回结果
     */
    ApiResult updateRecord(ReportCategoryTemplate record);

    /**
     * 更新模板运行时间记录
     *
     * @param entity
     */
    void updateTemplateTask(QuartzEntity entity);
}
