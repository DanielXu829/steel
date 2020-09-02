package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TemplateTypeEnum {
    EXCEL(0),
    WORD(1);

    private final int code;

    public static TemplateTypeEnum getByCode(int code) {
        for (TemplateTypeEnum templateTypeEnum : TemplateTypeEnum.values()) {
            if (code == templateTypeEnum.code) {
                return templateTypeEnum;
            }
        }
        throw new LeafException("模板类型不存在");
    }
}
