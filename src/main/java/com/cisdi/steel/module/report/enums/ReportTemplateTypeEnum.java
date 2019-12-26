package com.cisdi.steel.module.report.enums;

import com.cisdi.steel.common.exception.LeafException;

/**
 *
 * <p>Description:  报表类型  </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum ReportTemplateTypeEnum {
    report_hour("report_hour", "小时"),
    report_four_hour("4hour_report", "4小时"),
    report_class("report_class", "班报表"),
    report_day("report_day", "日报表"),
    report_week("report_week", "周报表"),
    report_month("report_month", "月报表"),
    report_year("report_year", "年报表");

    /**
     * 编码值
     */
    private String code;
    /**
     * 生成文件的名称
     */
    private String name;

    ReportTemplateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    /**
     * 通过编码 获取执行的 枚举
     *
     * @param code 编码
     * @return 对应的枚举
     */
    public static ReportTemplateTypeEnum getType(String code) {
        ReportTemplateTypeEnum[] values = ReportTemplateTypeEnum.values();
        for (ReportTemplateTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new LeafException("无法获取类型");
    }

    @Override
    public String toString() {
        return this.code;
    }
}
