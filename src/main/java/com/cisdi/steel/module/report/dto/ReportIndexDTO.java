package com.cisdi.steel.module.report.dto;

import com.cisdi.steel.module.report.entity.ReportIndex;
import lombok.Data;

import java.util.List;

/**
 * 报表首页数据
 */
@Data
public class ReportIndexDTO {
    /**
     * 昨天日报
     */
    private List<ReportIndex> yestarDayList;
    /**
     * 今日日报
     */
    private List<ReportIndex> todayList;
    /**
     * 本月月报
     */
    private List<ReportIndex> monthList;
    /**
     * 其他最新报表
     */
    private List<ReportIndex> otherList;
}
