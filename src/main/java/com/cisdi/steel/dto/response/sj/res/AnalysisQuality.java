package com.cisdi.steel.dto.response.sj.res;

import java.math.BigDecimal;

public class AnalysisQuality {
    private String item;
    private BigDecimal low;
    private BigDecimal up;
    private BigDecimal center;
    private BigDecimal range;
    private BigDecimal firstGrade;
    private String unit;
    private String itemOs;
    private BigDecimal targetValue;
    private BigDecimal incentiveValue;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getUp() {
        return up;
    }

    public void setUp(BigDecimal up) {
        this.up = up;
    }

    public BigDecimal getCenter() {
        return center;
    }

    public void setCenter(BigDecimal center) {
        this.center = center;
    }

    public BigDecimal getRange() {
        return range;
    }

    public void setRange(BigDecimal range) {
        this.range = range;
    }

    public BigDecimal getFirstGrade() {
        return firstGrade;
    }

    public void setFirstGrade(BigDecimal firstGrade) {
        this.firstGrade = firstGrade;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemOs() {
        return itemOs;
    }

    public void setItemOs(String itemOs) {
        this.itemOs = itemOs;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getIncentiveValue() {
        return incentiveValue;
    }

    public void setIncentiveValue(BigDecimal incentiveValue) {
        this.incentiveValue = incentiveValue;
    }
}
