package com.cisdi.steel.dto.response.gl.res;

import java.math.BigDecimal;

public class TagValue extends TagValueKey {
    private BigDecimal val;

    public BigDecimal getVal() {
        return val;
    }

    public void setVal(BigDecimal val) {
        this.val = val;
    }
}