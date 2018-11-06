package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>Description: 报表文件-索引 服务类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface ReportIndexService extends IBaseService<ReportIndex> {

    /**
     * 文件上传
     *
     * @param file 需要上传的文件
     * @return 返回文件的保存目录
     */
    ApiResult upload(MultipartFile file);

    /**
     * 分页列表
     *
     * @param record 记录
     * @return 结果
     */
    ApiResult pageList(ReportIndexQuery record);

    /**
     * 添加数据
     *
     * @param reportIndex       编码
     */
    void insertReportRecord(ReportIndex reportIndex);

    /**
     * 添加数据
     *
     * @param code       编码
     * @param resultPath 文件存储的路径
     * @param category   分类名
     * @param indexType   类型
     * @param indexLang   语言
     */
    void insertReportRecord(String code, String resultPath, String category, String indexType, String indexLang);
}
