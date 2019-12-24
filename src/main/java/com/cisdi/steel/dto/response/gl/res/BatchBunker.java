package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;
import java.util.Date;

public class BatchBunker extends BatchBunkerKey {
    private String brandcode;

    private Date inputtime;

    private Date outputtime;

    private BigDecimal weightset;

    private BigDecimal weightwet;

    private BigDecimal weightdry;

    private BigDecimal moisture;

    private BigDecimal err;

    private BigDecimal speed;

    private String stackno;

    public String getBrandcode() {
        return brandcode;
    }

    public void setBrandcode(String brandcode) {
        this.brandcode = brandcode == null ? null : brandcode.trim();
    }

    public Date getInputtime() {
        return inputtime;
    }

    public void setInputtime(Date inputtime) {
        this.inputtime = inputtime;
    }

    public Date getOutputtime() {
        return outputtime;
    }

    public void setOutputtime(Date outputtime) {
        this.outputtime = outputtime;
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

    public BigDecimal getErr() {
        return err;
    }

    public void setErr(BigDecimal err) {
        this.err = err;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public String getStackno() {
        return stackno;
    }

    public void setStackno(String stackno) {
        this.stackno = stackno == null ? null : stackno.trim();
    }
}