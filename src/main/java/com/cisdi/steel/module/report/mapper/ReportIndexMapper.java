package com.cisdi.steel.module.report.mapper;

import com.cisdi.steel.module.report.query.ReportIndexQuery;
import org.apache.ibatis.annotations.Mapper;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
