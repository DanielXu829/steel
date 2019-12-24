package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;

public class BatchDistribution extends BatchDistributionKey {
    private String typ;

    private BigDecimal angleset;

    private BigDecimal angleact;

    private BigDecimal roundset;

    private BigDecimal roundact;

    private BigDecimal weightset;

    private BigDecimal weightact;

    private BigDecimal step;

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ == null ? null : typ.trim();
    }

    public BigDecimal getAngleset() {
        return angleset;
    }

    public void setAngleset(BigDecimal angleset) {
        this.angleset = angleset;
    }

    public BigDecimal getAngleact() {
        return angleact;
    }

    public void setAngleact(BigDecimal angleact) {
        this.angleact = angleact;
    }

    public BigDecimal getRoundset() {
        return roundset;
    }

    public void setRoundset(BigDecimal roundset) {
        this.roundset = roundset;
    }

    public BigDecimal getRoundact() {
        return roundact;
    }

    public void setRoundact(BigDecimal roundact) {
        this.roundact = roundact;
    }

    public BigDecimal getWeightset() {
        return weightset;
    }

    public void setWeightset(BigDecimal weightset) {
        this.weightset = weightset;
    }

    public BigDecimal getWeightact() {
        return weightact;
    }

    public void setWeightact(BigDecimal weightact) {
        this.weightact = weightact;
    }

    public BigDecimal getStep() {
        return step;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }
}