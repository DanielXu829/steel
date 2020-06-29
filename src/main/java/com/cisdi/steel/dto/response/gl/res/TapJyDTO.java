package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TapJyDTO {
    /**
     * 目标值
     */
    private Double targetValue;
    /**
     * 分子
     */
    private Integer fz;
    /**
     * 分母
     */
    private Integer fm;
}
