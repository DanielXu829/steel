package com.cisdi.steel.dto.response.sj;

import java.math.BigDecimal;
import java.util.List;

/**
 * 换堆操业会-配矿配比对比表实体
 */
public class BurdenBleRatio {
    private String pile_no;
    private Integer orderNum;
    private List<BurdenMatRatio> ratios;
    private BigDecimal sum;
    public String getPile_no() {
        return pile_no;
    }

    public void setPile_no(String pile_no) {
        this.pile_no = pile_no;
    }

    public List<BurdenMatRatio> getRatios() {
        return ratios;
    }

    public void setRatios(List<BurdenMatRatio> ratios) {
        this.ratios = ratios;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
