package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;

public class ChargeVarDistribution {
    private Integer matrixNo;

    private Short position;

    private BigDecimal angle;

    private BigDecimal round;

    private String back1;

    private Short typ;

    public Integer getMatrixNo() {
        return matrixNo;
    }

    public void setMatrixNo(Integer matrixNo) {
        this.matrixNo = matrixNo;
    }

    public Short getPosition() {
        return position;
    }

    public void setPosition(Short position) {
        this.position = position;
    }

    public BigDecimal getAngle() {
        return angle;
    }

    public void setAngle(BigDecimal angle) {
        this.angle = angle;
    }

    public BigDecimal getRound() {
        return round;
    }

    public void setRound(BigDecimal round) {
        this.round = round;
    }

    public String getBack1() {
        return back1;
    }

    public void setBack1(String back1) {
        this.back1 = back1 == null ? null : back1.trim();
    }

    public Short getTyp() {
        return typ;
    }

    public void setTyp(Short typ) {
        this.typ = typ;
    }
}