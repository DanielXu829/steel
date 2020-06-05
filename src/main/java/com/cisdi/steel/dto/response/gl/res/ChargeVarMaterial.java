package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;

public class ChargeVarMaterial {
    private Integer matrixNo;

    private String brandCode;

    private String brandName;

    private BigDecimal weight;

    private Short typ;

    public Integer getMatrixNo() {
        return matrixNo;
    }

    public void setMatrixNo(Integer matrixNo) {
        this.matrixNo = matrixNo;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode == null ? null : brandCode.trim();
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Short getTyp() {
        return typ;
    }

    public void setTyp(Short typ) {
        this.typ = typ;
    }
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}