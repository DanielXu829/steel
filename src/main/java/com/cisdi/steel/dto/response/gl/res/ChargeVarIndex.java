package com.cisdi.steel.dto.response.gl.res;

import java.util.Date;

public class ChargeVarIndex {
    private Integer matrixNo;

    private Date time;

    private Integer chargeNo;

    private String back1;

    private String back2;

    private String back3;

    private Short typ;

    public Integer getMatrixNo() {
        return matrixNo;
    }

    public void setMatrixNo(Integer matrixNo) {
        this.matrixNo = matrixNo;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getChargeNo() {
        return chargeNo;
    }

    public void setChargeNo(Integer chargeNo) {
        this.chargeNo = chargeNo;
    }

    public String getBack1() {
        return back1;
    }

    public void setBack1(String back1) {
        this.back1 = back1 == null ? null : back1.trim();
    }

    public String getBack2() {
        return back2;
    }

    public void setBack2(String back2) {
        this.back2 = back2 == null ? null : back2.trim();
    }

    public String getBack3() {
        return back3;
    }

    public void setBack3(String back3) {
        this.back3 = back3 == null ? null : back3.trim();
    }

    public Short getTyp() {
        return typ;
    }

    public void setTyp(Short typ) {
        this.typ = typ;
    }
}