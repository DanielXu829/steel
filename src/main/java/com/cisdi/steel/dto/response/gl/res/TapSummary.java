package com.cisdi.steel.dto.response.gl.res;

import lombok.Data;

@Data
public class TapSummary {
    /**
     * 工作日期
     */
    private String workDate;
    /**
     * 班次。null全天，1夜班，2白班
     */
    private String workShift;
    /**
     * 出铁次数
     */
    private Integer tapNum;
    /**
     * 理论铁量
     */
    private Double theoryWeight;
    /**
     * 实际铁量
     */
    private Double actWeight;
    /**
     * 铁水平均温度
     */
    private Double tapTemp;
    /**
     * 出铁比率
     */
    private Double hmRatio;
    /**
     * 出渣比率
     */
    private Double slagRatio;
    /**
     * 出铁总耗时
     */
    private Double hmDuration;
    /**
     * 出渣总耗时
     */
    private Double slagDuration;

    /**
     * 喷溅比率
     */
    private Double spatterRatio;

    /**
     * 放干渣次数
     */
    private Double drySlagNum;

    /**
     * 毛重
     */
    private Double grossWgt;
    /**
     * 皮重
     */
    private Double tareWgt;
    /**
     * 铁水流速
     */
    private Double tapSpeed;
    /**
     * 见渣率
     */
    private Double slagPercent;
}
