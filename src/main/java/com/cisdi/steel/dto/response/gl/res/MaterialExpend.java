package com.cisdi.steel.dto.response.gl.res;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MaterialExpend {
    private String workDate;
    private String prodUnitCode;
    private String matCode;
    private String matCname;
    private BigDecimal wetWgt;
    private BigDecimal dryWgt;
    private BigDecimal h20;
}
