package com.cisdi.steel.dto.response.gl.res;

import java.util.Date;

public class BfBlastMain {
    private Long id;

    private String blastNo;

    private Double blastLength;

    private Double blastRound;

    private Double blastDiameter;

    private Date blastChangeTime;

    private String blastFactory;

    private String reason;

    private String status;

    private Double flowDifference;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlastNo() {
        return blastNo;
    }

    public void setBlastNo(String blastNo) {
        this.blastNo = blastNo == null ? null : blastNo.trim();
    }

    public Double getBlastLength() {
        return blastLength;
    }

    public void setBlastLength(Double blastLength) {
        this.blastLength = blastLength;
    }

    public Double getBlastRound() {
        return blastRound;
    }

    public void setBlastRound(Double blastRound) {
        this.blastRound = blastRound;
    }

    public Double getBlastDiameter() {
        return blastDiameter;
    }

    public void setBlastDiameter(Double blastDiameter) {
        this.blastDiameter = blastDiameter;
    }

    public Date getBlastChangeTime() {
        return blastChangeTime;
    }

    public void setBlastChangeTime(Date blastChangeTime) {
        this.blastChangeTime = blastChangeTime;
    }

    public String getBlastFactory() {
        return blastFactory;
    }

    public void setBlastFactory(String blastFactory) {
        this.blastFactory = blastFactory == null ? null : blastFactory.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Double getFlowDifference() {
        return flowDifference;
    }

    public void setFlowDifference(Double flowDifference) {
        this.flowDifference = flowDifference;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}