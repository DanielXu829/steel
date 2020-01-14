package com.cisdi.steel.dto.response.sj.res;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class ProdStopRecord implements Serializable {
    private Date startTime;

    private Date endTime;

    private String prodUnitCode;

    private Long stopTime;

    private String stopReason;

    private String causeDesc;

    private String workShift;

    private String workTeam;

    private Date lastModifyTime;

    private Short delFlag;

    private static final long serialVersionUID = 1L;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProdUnitCode() {
        return prodUnitCode;
    }

    public void setProdUnitCode(String prodUnitCode) {
        this.prodUnitCode = prodUnitCode;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public String getCauseDesc() {
        return causeDesc;
    }

    public void setCauseDesc(String causeDesc) {
        this.causeDesc = causeDesc;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }

    public String getWorkTeam() {
        return workTeam;
    }

    public void setWorkTeam(String workTeam) {
        this.workTeam = workTeam;
    }

    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public Short getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Short delFlag) {
        this.delFlag = delFlag;
    }
}