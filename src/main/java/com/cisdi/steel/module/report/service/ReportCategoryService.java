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
     * 查询报表分类，无叶子节点
     *
     * @return 所有菜单
     */
    ApiResult<List<ReportCategory>> selectAllCategoryNoLeaf(ReportCategory record);

    /**
     * 查询 生成模板需要的信息
     *
     * @param code 编码
     * @return 结果
     */
    ReportPathDTO selectReportInfoByCode(String code);

    /**
     * 插入数据
     *
     * @param record 数据
     * @param sequence 单个工序
     * @return
     */
    ApiResult insertRecord(ReportCategory record, String sequence);

    /**
     * 递归删除当前节点
     * @param record
     * @return
     */
    ApiResult deleteCurrentTarget(ReportCategory record);
}
