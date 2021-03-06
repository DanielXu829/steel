package com.cisdi.steel.module.job.enums;

/**
 * 时间单位
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/12/27 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum TimeUnitEnum {
    // 秒
    SECOND("SECOND"),
    // 分钟
    MINUTE("MINUTE"),
    // 小时
    HOUR("HOUR"),
    // 天
    DATE("DATE"),
    // 月
    MONTH("MONTH");

    private String unit;

    TimeUnitEnum(String unit) {
        this.unit = unit;
    }

    public static TimeUnitEnum getValues(String unit) {
        TimeUnitEnum[] values = TimeUnitEnum.values();
        for (TimeUnitEnum value : values) {
            if (value.unit.equals(unit)) {
                return value;
            }
        }
        return null;
    }
}
