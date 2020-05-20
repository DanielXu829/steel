package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

@Data
public class YardRunInfo {
    //取料
    private long pullMat;
    //堆料
    private long pushMat;

    private Integer pullMatCount;
    private Integer pushMatCount;
    private long pullMatTime;
    private long pushMatTime;
}
