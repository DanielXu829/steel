package com.cisdi.steel.module.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.query.ReportIndexQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>Description: 报表文件-索引 Mapper 接口 </p>
 * <P>Date: 2018-10-24 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Mapper
public interface ReportIndexMapper extends BaseMapper<ReportIndex> {

    /**
     * 通过多参数更新报表
     *
     * @param reportIndex
     * @return
     */
    int updateByMoreParamter(ReportIndex reportIndex);

    /**
     * 通过条件查询id
     *
     * @param reportIndex 条件
     * @return 结果
     */
    ReportIndex selectIdByParamter(ReportIndex reportIndex);

    /**
     * 查询最新一条
     *
     * @param reportIndex
     */
    ReportIndex selectIdByParamter1(ReportIndex reportIndex);

    /**
     * 通过编码查询最新的一条数据
     *
     * @param code
     * @return
     */
    ReportIndex queryLastOne(@Param("code") String code);

    /**
     * 查询今天、昨天报表
     *
     * @param reportIndexQuery
     * @return
     */
    List<ReportIndex> queryReportToday(ReportIndexQuery reportIndexQuery);

    /**
     * 查询指定报表
     * @param code
     * @param startTime
     * @param endTime
     * @return
     */
    List<ReportIndex> queryReport(@Param("code") String code, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 查询本月报表
     *
     * @param reportIndexQuery
     * @return
     */
    List<ReportIndex> queryReportMonth(ReportIndexQuery reportIndexQuery);

    /**
     * 查询其他报表
     *
     * @param reportIndexQuery
     * @return
     */
    List<ReportIndex> queryReportOther(ReportIndexQuery reportIndexQuery);

}
