package com.cisdi.steel.dto.response.sj.res;

import java.util.List;

public class ProdStopRecordInfo {
    private List<ProdStopRecord> dayProdStopRecords;
    private List<ProdStopRecord> nightProdStopRecords;
    //次数
    private Integer time;
    // 总和
    private Integer totalStopTime;

    public List<ProdStopRecord> getDayProdStopRecords() {
        return dayProdStopRecords;
    }

    public void setDayProdStopRecords(List<ProdStopRecord> dayProdStopRecords) {
        this.dayProdStopRecords = dayProdStopRecords;
    }

    public List<ProdStopRecord> getNightProdStopRecords() {
        return nightProdStopRecords;
    }

    public void setNightProdStopRecords(List<ProdStopRecord> nightProdStopRecords) {
        this.nightProdStopRecords = nightProdStopRecords;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTotalStopTime() {
        return totalStopTime;
    }

    public void setTotalStopTime(Integer totalStopTime) {
        this.totalStopTime = totalStopTime;
    }
}
