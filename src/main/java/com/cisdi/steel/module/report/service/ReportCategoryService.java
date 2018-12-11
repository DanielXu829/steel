package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.dto.ReportPathDTO;
import com.cisdi.steel.module.report.entity.ReportCategory;

import java.util.List;

/**
 * <p>Description: 报表分类 服务类 </p>
 * <P>Date: 2018-10-23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface ReportCategoryService extends IBaseService<ReportCategory> {

    /**
     * 查询菜单列表
     *
     * @return 所有菜单
     */
    ApiResult<List<ReportCategory>> selectAllCategory(ReportCategory record);


    /**
     * 查询 生成模板需要的信息
     *
     * @param code 编码
     * @return 结果
     */
    ReportPathDTO selectReportInfoByCode(String code);

}
