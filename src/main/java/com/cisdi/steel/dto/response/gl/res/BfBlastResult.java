package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;
import java.util.Date;

public class BfBlastResult {
    private Long id;

    private Date blastTime;

    private Integer blastLongCount;

    private Integer blastShortCount;

    private Integer blastChangeCount;

    private String currentUrl;

    private BigDecimal blastArea;

    private String createUser;

    private Integer blastCycle;

    private Integer attr1;

    private Integer attr2;

    private Integer attr3;

    private Integer attr4;

    private Date createTime;

    private Integer tuyereBlockCount;

    private Integer tuyereLeakCount;

    private Integer tuyereQualityCount;

    private Integer tuyereBurnoutCount;

    private Integer tuyereAdjustCount;

    private Integer tuyereAbrasionCount;

    private Integer tuyereOutboardCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBlastTime() {
        return blastTime;
    }

    public void setBlastTime(Date blastTime) {
        this.blastTime = blastTime;
    }

    public Integer getBlastLongCount() {
        return blastLongCount;
    }

    public void setBlastLongCount(Integer blastLongCount) {
        this.blastLongCount = blastLongCount;
    }

    public Integer getBlastShortCount() {
        return blastShortCount;
    }

    public void setBlastShortCount(Integer blastShortCount) {
        this.blastShortCount = blastShortCount;
    }

    public Integer getBlastChangeCount() {
        return blastChangeCount;
    }

    public void setBlastChangeCount(Integer blastChangeCount) {
        this.blastChangeCount = blastChangeCount;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl == null ? null : currentUrl.trim();
    }

    public BigDecimal getBlastArea() {
        return blastArea;
    }

    public void setBlastArea(BigDecimal blastArea) {
        this.blastArea = blastArea;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Integer getBlastCycle() {
        return blastCycle;
    }

    public void setBlastCycle(Integer blastCycle) {
        this.blastCycle = blastCycle;
    }

    public Integer getAttr1() {
        return attr1;
    }

    public void setAttr1(Integer attr1) {
        this.attr1 = attr1;
    }

    public Integer getAttr2() {
        return attr2;
    }

    public void setAttr2(Integer attr2) {
        this.attr2 = attr2;
    }

    public Integer getAttr3() {
        return attr3;
    }

    public void setAttr3(Integer attr3) {
        this.attr3 = attr3;
    }

    public Integer getAttr4() {
        return attr4;
    }

    public void setAttr4(Integer attr4) {
        this.attr4 = attr4;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTuyereBlockCount() {
        return tuyereBlockCount;
    }

    public void setTuyereBlockCount(Integer tuyereBlockCount) {
        this.tuyereBlockCount = tuyereBlockCount;
    }

    public Integer getTuyereLeakCount() {
        return tuyereLeakCount;
    }

    public void setTuyereLeakCount(Integer tuyereLeakCount) {
        this.tuyereLeakCount = tuyereLeakCount;
    }

    public Integer getTuyereQualityCount() {
        return tuyereQualityCount;
    }

    public void setTuyereQualityCount(Integer tuyereQualityCount) {
        this.tuyereQualityCount = tuyereQualityCount;
    }

    public Integer getTuyereBurnoutCount() {
        return tuyereBurnoutCount;
    }

    public void setTuyereBurnoutCount(Integer tuyereBurnoutCount) {
        this.tuyereBurnoutCount = tuyereBurnoutCount;
    }

    public Integer getTuyereAdjustCount() {
        return tuyereAdjustCount;
    }

    public void setTuyereAdjustCount(Integer tuyereAdjustCount) {
        this.tuyereAdjustCount = tuyereAdjustCount;
    }

    public Integer getTuyereAbrasionCount() {
        return tuyereAbrasionCount;
    }

    public void setTuyereAbrasionCount(Integer tuyereAbrasionCount) {
        this.tuyereAbrasionCount = tuyereAbrasionCount;
    }

    public Integer getTuyereOutboardCount() {
        return tuyereOutboardCount;
    }

    public void setTuyereOutboardCount(Integer tuyereOutboardCount) {
        this.tuyereOutboardCount = tuyereOutboardCount;
    }
}