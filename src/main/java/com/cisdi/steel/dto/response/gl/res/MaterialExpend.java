package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialExpend {
    private String workDate;
    private String workShift;
    private String matCode;
    private String matCname;
    private BigDecimal wetWgt;
    private BigDecimal dryWgt;
    private BigDecimal h2o;
    private BigDecimal actRate;
}
