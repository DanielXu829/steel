package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class YardRunInfoOf26B {
    //取料-堆6-天
    private BigDecimal pullMat6ForD;
    //堆料-堆2-天
    private BigDecimal pushMat2ForD;
    //堆区库存-b区-天
    private BigDecimal yardInventoryBForD;
    //取料-堆6-白班
    private BigDecimal pullMat6ForShiftD;
    //堆料-堆2-白班
    private BigDecimal pushMat2ForShiftD;
    //堆区库存-b区-白班
    private BigDecimal yardInventoryBForShiftD;
    //取料-堆6-夜班
    private BigDecimal pullMat6ForShiftN;
    //堆料-堆2-夜班
    private BigDecimal pushMat2ForShiftN;
    //堆区库存-b区-夜班
    private BigDecimal yardInventoryBForShiftN;
}
