package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;

public enum TimeDivideEnum {
    HOUR("时", "1h", 1),
    DAY("日", "1d", 2),
    MONTH("月", "1d",3),
    ;

    private String divideType;
    private String timeType;
    private Integer code;

    TimeDivideEnum(String divideType, String timeType, Integer code) {
        this.divideType = divideType;
        this.timeType = timeType;
        this.code = code;
    }

    public static TimeDivideEnum getEnumByCode(Integer code) {
        TimeDivideEnum[] values = TimeDivideEnum.values();
        for (TimeDivideEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new LeafException("时间划分类型不存在");
    }

    public String getDivideType() {
        return divideType;
    }

    public String getTimeType() {
        return timeType;
    }

    public Integer getCode() {
        return code;
    }
}
