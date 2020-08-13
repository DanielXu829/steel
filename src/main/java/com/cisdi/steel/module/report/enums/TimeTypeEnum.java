package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;

public enum TimeTypeEnum {
    TIME_RANGE("时间范围",  "1"),
    RECENT_TIME("最近多少时间", "2"),
    ;

    private String name;
    private String code;

    TimeTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static TimeTypeEnum getEnumByCode(String code) {
        TimeTypeEnum[] values = TimeTypeEnum.values();
        for (TimeTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new LeafException("时间查询类型不存在");
    }

    public String getCode() {
        return code;
    }
}
