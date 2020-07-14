package com.cisdi.steel.dto.response.sj.res;

import lombok.Data;

import java.util.List;

@Data
public class ProdStopRecordInfo {
    private List<ProdStopRecord> dayProdStopRecords;
    private List<ProdStopRecord> nightProdStopRecords;
    // 全天停机次数
    private Integer time;
    // 为白班停机次数
    private Integer timeOfDay;
    // 夜班停机次数
    private Integer timeOfNight;
    // 全天停机时间总和
    private Integer totalStopTime;
    // 白班停机时间总和
    private Integer totalStopTimeOfDay;
    // 夜班停机时间总和
    private Integer totalStopTimeOfNight;
}
