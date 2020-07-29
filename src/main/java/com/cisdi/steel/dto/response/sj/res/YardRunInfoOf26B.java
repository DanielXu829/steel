package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

@Data
public class YardRunInfoOf26B {
    // 堆 6-天
    private long pullMat6ForD;
    // 堆 2-天
    private long pushMat2ForD;
    // 库存-天
    private long yardInventoryBForD;
    // 堆 6-白班
    private long pullMat6ForShiftD;
    // 堆 2-白班
    private long pushMat2ForShiftD;
    // 库存-白班
    private long yardInventoryBForShiftD;
    // 堆 6-夜班
    private long pullMat6ForShiftN;
    // 堆 2-夜班
    private long pushMat2ForShiftN;
    // 库存-夜班
    private long yardInventoryBForShiftN;
}
