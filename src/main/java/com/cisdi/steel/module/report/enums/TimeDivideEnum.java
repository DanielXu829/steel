package com.cisdi.steel.module.report.enums;

public enum TimeDivideEnum {
    HOUR("小时", "1h", "avg", 1),
    DAY("天", "1d", "avg", 2),
    ;

    private String divideType;
    private String timeType;
    private String calType;
    private Integer code;

    TimeDivideEnum(String divideType, String timeType, String calType, Integer code) {
        this.divideType = divideType;
        this.timeType = timeType;
        this.calType = calType;
        this.code = code;
    }

    public String getDivideType() {
        return divideType;
    }

    public String getTimeType() {
        return timeType;
    }

    public String getCalType() {
        return calType;
    }

    public Integer getCode() {
        return code;
    }
}
