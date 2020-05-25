package com.cisdi.steel.dto.response.sj;

import org.apache.ibatis.type.JdbcType;
import java.math.BigDecimal;

public class ProcessGoal {

    private Long cardId;

    private Long paramId;

    private BigDecimal low;

    private BigDecimal up;

    private String controlAction;

    /**
     * @return CARD_ID
     */
    public Long getCardId() {
        return cardId;
    }

    /**
     * @param cardId
     */
    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    /**
     * @return PARAM_ID
     */
    public Long getParamId() {
        return paramId;
    }

    /**
     * @param paramId
     */
    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getUp() {
        return up;
    }

    public void setUp(BigDecimal up) {
        this.up = up;
    }

    /**
     * @return CONTROL_ACTION
     */
    public String getControlAction() {
        return controlAction;
    }

    /**
     * @param controlAction
     */
    public void setControlAction(String controlAction) {
        this.controlAction = controlAction;
    }
}