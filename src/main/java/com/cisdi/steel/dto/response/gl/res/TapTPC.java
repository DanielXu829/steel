package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TapTPC {
    private Date clockTime;
    private String workShift;
    private String tapno;
    private String tpcNo;
    private BigDecimal grossWt;
    private BigDecimal tareWt;
    private BigDecimal netWt;
}
