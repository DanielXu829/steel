package com.cisdi.steel.dto.response.sj.res;

import java.math.BigDecimal;

public class AnaQualitySttcs {
    private String item;
    private Integer total;
    private Integer unqualified;
    private BigDecimal qualifiedRate;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getUnqualified() {
        return unqualified;
    }

    public void setUnqualified(Integer unqualified) {
        this.unqualified = unqualified;
    }

    public BigDecimal getQualifiedRate() {
        return qualifiedRate;
    }

    public void setQualifiedRate(BigDecimal qualifiedRate) {
        this.qualifiedRate = qualifiedRate;
    }
}
