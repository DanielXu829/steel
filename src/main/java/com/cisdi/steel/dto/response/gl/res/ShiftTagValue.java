package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带班次的tagValue
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTagValue {
    private int id;
    private long clockTime;
    private String workShift;
    private double val;
}
