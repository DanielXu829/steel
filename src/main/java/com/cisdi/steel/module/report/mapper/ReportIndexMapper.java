package com.cisdi.steel.module.report.mapper;

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
     * 查询今天、昨天报表
     *
     * @param toDay
     * @return
     */
    List<ReportIndex> queryReportToday(@Param("toDay") String toDay);

    /**
     * 查询本月报表
     *
     * @param toDay
     * @return
     */
    List<ReportIndex> queryReportMonth(@Param("toDay") String toDay);

    /**
     * 查询其他报表
     *
     * @param toDay
     * @return
     */
    List<ReportIndex> queryReportOther(@Param("toDay") String toDay);

}
