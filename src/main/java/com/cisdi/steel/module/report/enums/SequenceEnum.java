package com.cisdi.steel.module.report.enums;

/**
 * 工序和excel版本对应关系
 */
public enum SequenceEnum {
    // 秒
    GL8("8高炉", "8.0"),
    // 分钟
    SJ4("4烧结", "4"),
    // 小时
    JH910("焦化910", "910");

    private String sequenceCode;
    private String version;

    SequenceEnum(String sequenceCode, String version) {
        this.sequenceCode = sequenceCode;
        this.version = version;
    }

    public static String getVersion(String sequenceCode) {
        SequenceEnum[] values = SequenceEnum.values();
        for (SequenceEnum value : values) {
            if (value.sequenceCode.equals(sequenceCode)) {
                return value.version;
            }
        }
        return null;
    }

    public String getSequenceCode(){
        return this.sequenceCode;
    }

    public String getVersion(){
        return this.version;
    }
}