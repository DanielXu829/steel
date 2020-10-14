package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;

/**
 * 工序和excel版本对应关系
 */
public enum SequenceEnum {
    GL8("8高炉", "8.0"),
    GL7("7高炉", "7.0"),
    SJ4("4烧结", "4"),
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

    public static SequenceEnum getSequenceEnumByCode(String sequenceCode) {
        SequenceEnum[] values = SequenceEnum.values();
        for (SequenceEnum value : values) {
            if (value.getSequenceCode().equals(sequenceCode)) {
                return value;
            }
        }
        throw new LeafException("工序类型不存在");
    }

    public String getSequenceCode(){
        return this.sequenceCode;
    }

    public String getVersion(){
        return this.version;
    }
}