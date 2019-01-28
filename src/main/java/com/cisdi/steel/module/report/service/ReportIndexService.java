package com.cisdi.steel.module.report.service;

import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>Description: 报表文件-索引 服务类 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public interface ReportIndexService extends IBaseService<ReportIndex> {

    /**
     * 通过路径查询相关信息
     *
     * @param path
     * @return
     */
    ReportIndex queryByPath(String path);

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
     * @param reportIndex 编码
     */
    void insertReportRecord(ReportIndex reportIndex);

    /**
     * 添加数据
     *
     * @param code       编码
     * @param resultPath 文件存储的路径
     * @param category   分类名
     * @param indexType  类型
     * @param indexLang  语言
     */
    void insertReportRecord(String code, String resultPath, String category, String indexType, String indexLang);

    /**
     * 报表首页数据
     *
     * @return
     */
    ApiResult reportIndex(ReportIndexQuery reportIndexQuery);

    /**
     * 判断当天的模板是否存在
     *
     * @param reportIndex 索引数据
     * @return 生成文件位置 or null
     */
    String existTemplate(ReportIndex reportIndex);


    /**
     * 判断当天的模板是否存在
     *
     * @param reportIndex 索引数据
     * @return 生成文件位置 or null
     */
    ReportIndex existTemplate1(ReportIndex reportIndex);
}
