package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplateTypeEnum {
    EXCEL(0, ".xlsx"),
    WORD(1, ".docx");

    private final int code;
    private final String endSuffix;

    public static TemplateTypeEnum getByCode(int code) {
        for (TemplateTypeEnum templateTypeEnum : TemplateTypeEnum.values()) {
            if (code == templateTypeEnum.code) {
                return templateTypeEnum;
            }
        }
        throw new LeafException("模板类型不存在,请传入0或者1");
    }
}
