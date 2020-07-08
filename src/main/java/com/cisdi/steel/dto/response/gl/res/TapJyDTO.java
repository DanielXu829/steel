package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TapJyDTO {
    /**
     * 班次
     * 1:夜班，2:白班
     */
    private String workShift;
    /**
     * 目标值
     */
    private Double targetVal;
    /**
     * 分子
     */
    private Integer fz;
    /**
     * 分母
     */
    private Integer fm;

    /**
     *  铁量
     */
    private BigDecimal tl;
}
