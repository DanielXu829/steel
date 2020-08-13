package com.cisdi.steel.module.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum TagCalSuffixEnum {
    avg("avg", "平均值"),
    cur("cur", "当前值"),
    min("min", "最小值"),
    max("max", "最大值"),
    evt("evt", "事件值");

    public String code;
    public String name;

    public static List<String> getTagCalSuffixCodeList() {
        List<String> codeList = new ArrayList<String>();
        for (TagCalSuffixEnum value : TagCalSuffixEnum.values()) {
            codeList.add(value.code);
        }
        return codeList;
    }

    TagCalSuffixEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
