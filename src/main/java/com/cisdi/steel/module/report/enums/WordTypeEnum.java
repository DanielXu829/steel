package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WordTypeEnum {
    PLAIN_TEXT(1, "纯文本"),
    LINE_CHART(2, "折线图");

    private final int code;
    private final String name;

    public static WordTypeEnum getByCode(int code) {
        for (WordTypeEnum wordTypeEnum : WordTypeEnum.values()) {
            if (code == wordTypeEnum.code) {
                return wordTypeEnum;
            }
        }
        throw new LeafException("word模板类型不存在");
    }
}
