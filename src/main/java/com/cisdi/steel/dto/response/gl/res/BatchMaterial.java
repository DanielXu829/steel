package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;

public class BatchMaterial extends BatchMaterialKey {
    private BigDecimal weightset;

    private BigDecimal weightwet;

    private BigDecimal weightdry;

    private BigDecimal moisture;

    private String descr;

    private Long anaidLg;

    private Long anaidLc;

    private Long anaidLp;

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public BigDecimal getWeightset() {
        return weightset;
    }

    public void setWeightset(BigDecimal weightset) {
        this.weightset = weightset;
    }

    public BigDecimal getWeightwet() {
        return weightwet;
    }

    public void setWeightwet(BigDecimal weightwet) {
        this.weightwet = weightwet;
    }

    public BigDecimal getWeightdry() {
        return weightdry;
    }

    public void setWeightdry(BigDecimal weightdry) {
        this.weightdry = weightdry;
    }

    public BigDecimal getMoisture() {
        return moisture;
    }

    public void setMoisture(BigDecimal moisture) {
        this.moisture = moisture;
    }

    public Long getAnaidLg() {
        return anaidLg;
    }

    public void setAnaidLg(Long anaidLg) {
        this.anaidLg = anaidLg;
    }

    public Long getAnaidLc() {
        return anaidLc;
    }

    public void setAnaidLc(Long anaidLc) {
        this.anaidLc = anaidLc;
    }

    public Long getAnaidLp() {
        return anaidLp;
    }

    public void setAnaidLp(Long anaidLp) {
        this.anaidLp = anaidLp;
    }
}