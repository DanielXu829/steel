package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplateBuildEnum {
    DynamicTemplate("1","动态报表模板"),
    NormalTemplate("0", "普通模板");

    private String code;
    private String name;

    public static TemplateBuildEnum getEnumByCode(String code) {
        for (TemplateBuildEnum value : TemplateBuildEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        throw new LeafException("报表构建枚举类型不存在");
    }

    /**
     * 是否是动态报表
     * @param code
     * @return
     */
    public static boolean isReportDynamic(String code) {
        return DynamicTemplate.code.equals(code);
    }
}
