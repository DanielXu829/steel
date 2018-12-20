package com.cisdi.steel.module.report.enums;

/**
 * <p>Description:   接口对应类型      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum ApiCodeEnum {

    api("api", "5烧结"),

    api2("api2", "6烧结"),

    cms("cms", "6高炉"),

    cms2("cms2", "8高炉"),

    energy("energy", "能介"),

    coking("coking", "焦化"),

    material("material", "原料场"),
    /**
     * 无
     */
    none("", "");

    /**
     * 所属编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;


    public static ApiCodeEnum getByCode(String code) {
        ApiCodeEnum[] values = ApiCodeEnum.values();
        for (ApiCodeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        // 默认无 防止错误
        return ApiCodeEnum.none;
    }

    ApiCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
