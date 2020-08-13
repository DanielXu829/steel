package com.cisdi.steel.module.report.enums;

import java.util.ArrayList;
import java.util.List;

public enum TagTimeSuffixEnum {
    OneMinute("1m", "1分钟"),
    OneHour("1h", "1小时"),
    TwentyHours("12h", "12小时"),
    OneDay("1d", "1天"),
    ;

    public String code;
    public String name;

    TagTimeSuffixEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static List<String> getTagTimeSuffixCodeList() {
        List<String> codeList = new ArrayList<String>();
        for (TagTimeSuffixEnum value : TagTimeSuffixEnum.values()) {
            codeList.add(value.code);
        }
        return codeList;
    }


    public String getTagTimeSuffix() {
        return code;
    }
}
