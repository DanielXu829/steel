package com.cisdi.steel.dto.response.sj;

import java.math.BigDecimal;

public class BurdenMatRatio {
    private String brandcode;
    private String brandname;
    private BigDecimal ratio;

    public String getBrandcode() {
        return brandcode;
    }

    public void setBrandcode(String brandcode) {
        this.brandcode = brandcode;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }
}
